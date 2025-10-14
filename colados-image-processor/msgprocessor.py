from fileprocessor import process_file
from msgproducer import produce_message
from repository import save_processed_file_details


def process_message(msg):
    print(f"Processing message: {msg}")

    status, results = process_file(msg.file_name)
    if status is None or results is None:
        print(f"Failed to process message: {msg}")
        return

    save_processed_file_details(msg.file_name, results, status)

    print(f"Finished processing message: {msg}")

    produce_message(msg.file_name, results, status)