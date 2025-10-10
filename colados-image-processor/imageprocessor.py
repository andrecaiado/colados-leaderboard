import os
from sys import exception
from inference_sdk import InferenceHTTPClient
from dotenv import load_dotenv
from filemngmt import delete_tmp_file, download_file_from_bucket
from msgproducer import produce_message
from repository import store_processed_file
from schemas import Status

load_dotenv()

inference_http_client = InferenceHTTPClient(
    api_url=os.getenv("API_URL", "https://serverless.roboflow.com"),
    api_key=os.getenv("API_KEY"),
)


def extract_player_data(ocr_prediction):
    value = ocr_prediction["class"]
    player_position = None
    player_char_name = None
    player_score = None
    try:
        value_num = int(float(value))
        if 1 <= value_num <= 12:  # Positions are between 1 and 12
            player_position = value_num
        else:
            player_score = value_num  # If it's not a position and it's numeric, it must be the score
    except ValueError:
        player_char_name = value  # If it's not numeric, it must be the character's name

    return {
        "position": player_position,
        "name": player_char_name,
        "score": player_score,
    }


def build_player_result(ocr_predictions):
    player = {"position": None, "name": None, "score": None}

    for pred in ocr_predictions["predictions"]:
        player_data = extract_player_data(pred)

        if player_data["position"] is not None:
            player["position"] = player_data["position"]
        elif player_data["name"] is not None:
            player["name"] = player_data["name"]
        elif player_data["score"] is not None:
            player["score"] = player_data["score"]

    print(
        f"Player result - Position: {player['position']}, Name: {player['name']}, Score: {player['score']}"
    )
    return player


def build_players_results(results):
    players_results = []
    result_ocr = results[0]["easy_ocr"]
    for item in result_ocr[0]:
        ocr_predictions = item["predictions"]
        players_results.append(build_player_result(ocr_predictions))

    return players_results


def analyze_image(file) -> dict | Exception:
    try:
        return inference_http_client.run_workflow(
            workspace_name=os.getenv("WORKSPACE_NAME"),
            workflow_id=os.getenv("WORKFLOW_ID"),
            images={"image2": file},
            use_cache=True,  # cache workflow definition for 15 minutes
        )
    except Exception as e:
        print(f"Error during image analysis: {e}")
        return e


def process_file(file_name):
    print(f"Processing file: {file_name}")
    file = download_file_from_bucket(file_name)
    if not file:
        return

    analysis_results = analyze_image(file)
    if isinstance(analysis_results, Exception):
        status = Status.FAILED
        results = {"exception": str(analysis_results)}
    else:
        status = Status.PROCESSED
        results = build_players_results(analysis_results)

    store_processed_file(file_name, results, status)

    delete_tmp_file(file_name)
    print(f"Finished processing file: {file_name}")

    produce_message(file_name, results, status)
