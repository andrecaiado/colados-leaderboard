from datetime import datetime, timezone
import json
import os
import time
from dotenv import load_dotenv
import pika
import signal
import sys
from schemas import FileProcessedMsg, ProcessedFile

env_path = os.path.abspath(os.path.join(os.path.dirname(__file__), "../.env"))
load_dotenv(env_path)


def connect():
    while True:
        try:
            connection = pika.BlockingConnection(
                pika.ConnectionParameters(host=os.getenv("RABBITMQ_HOST")),
            )
            channel = connection.channel()
            channel.queue_declare(
                queue=os.getenv("RABBITMQ_QUEUE_IMG_PROCESSED"), durable=True
            )
            return connection, channel
        except pika.exceptions.AMQPConnectionError as e:
            print(f"Connection to RabbitMQ failed: {e}. Retrying in 5 seconds...")
            time.sleep(5)


connection, channel = connect()


def produce_message(file_name, results, status):
    global connection, channel
    msg = FileProcessedMsg(file_name=file_name, processed_at=datetime.now(timezone.utc).isoformat(), results=results, status=status)
    while True:
        try:
            channel.basic_publish(
                exchange=os.getenv("RABBITMQ_EXCHANGE_IMG_PROCESSED"),
                routing_key=os.getenv("RABBITMQ_ROUTING_KEY_IMG_PROCESSED"),
                body=json.dumps(msg.__dict__),
            )
            print(f" [x] Sent message for file {file_name}")
            break
        except (
            pika.exceptions.AMQPConnectionError,
            pika.exceptions.StreamLostError,
        ) as e:
            print(f"Publish failed: {e}. Reconnecting...")
            try:
                connection.close()
            except Exception:
                pass
            connection, channel = connect()


def graceful_shutdown(signum, frame):
    print("Shutting down gracefully...")
    try:
        connection.close()
    except Exception:
        pass
    sys.exit(0)


signal.signal(signal.SIGTERM, graceful_shutdown)
signal.signal(signal.SIGINT, graceful_shutdown)
