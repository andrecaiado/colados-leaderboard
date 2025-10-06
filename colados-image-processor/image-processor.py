import os
from pydoc import text
from inference_sdk import InferenceHTTPClient
from dotenv import load_dotenv

load_dotenv()

client = InferenceHTTPClient(
    api_url=os.getenv("API_URL", "https://serverless.roboflow.com"),
    api_key=os.getenv("API_KEY")
)

result = client.run_workflow(
    workspace_name=os.getenv("WORKSPACE_NAME"),
    workflow_id=os.getenv("WORKFLOW_ID"),
    images={
        "image2": os.path.join(os.path.dirname(__file__), "inputs", "mk8d-sb-10.jpg")
    },
    use_cache=True # cache workflow definition for 15 minutes
)

class PlayerResult:
    def __init__(self, position, name, score):
        self.position = position
        self.name = name
        self.score = score

def extract_player_data(ocr_prediction):
    value = ocr_prediction['class']
    player_position = None
    player_char_name = None
    player_score = None
    try:
        value_num = int(float(value))
        if 1 <= value_num <= 12:  # Positions are between 1 and 12
            player_position = value_num
        else:
            player_score = value_num # If it's not a position and it's numeric, it must be the score
    except ValueError:
        player_char_name = value # If it's not numeric, it must be the character's name

    return {"position": player_position,
            "name": player_char_name,
            "score": player_score}

def build_player_result(ocr_predictions):
    players = []
    player = PlayerResult(None, None, None)

    for pred in ocr_predictions['predictions']:
        player_data = extract_player_data(pred)

        if player_data['position'] is not None:
            player.position = player_data['position']
        elif player_data['name'] is not None:
            player.name = player_data['name']
        elif player_data['score'] is not None:
            player.score = player_data['score']

    print(f"Player result - Position: {player.position}, Name: {player.name}, Score: {player.score}")
    return player
        

result_ocr = result[0]["easy_ocr"]
for item in result_ocr[0]:
    ocr_predictions = item['predictions']

    player_results = []
    player_results.append(build_player_result(ocr_predictions))