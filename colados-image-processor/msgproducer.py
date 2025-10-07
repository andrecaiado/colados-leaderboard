import json
import os
from dataclasses import dataclass
from dotenv import load_dotenv
import pika
import signal
import sys

load_dotenv()


@dataclass
class ImageProcessedMessage:
    file_name: str
    results: dict


connection = pika.BlockingConnection(
    pika.ConnectionParameters(host=os.getenv("RABBITMQ_HOST")),
)
channel = connection.channel()
channel.queue_declare(queue=os.getenv("RABBITMQ_QUEUE_IMG_PROCESSED"), durable=True)


def produce_message(file_name, results):

    msg = ImageProcessedMessage(file_name=file_name, results=results)
    channel.basic_publish(
        exchange=os.getenv("RABBITMQ_EXCHANGE_IMG_PROCESSED"),
        routing_key=os.getenv("RABBITMQ_ROUTING_KEY_IMG_PROCESSED"),
        body=json.dumps(msg.__dict__),
    )
    print(f" [x] Sent message for file {file_name}")


def graceful_shutdown(signum, frame):
    print("Shutting down gracefully...")
    connection.close()
    sys.exit(0)


signal.signal(signal.SIGTERM, graceful_shutdown)
signal.signal(signal.SIGINT, graceful_shutdown)
