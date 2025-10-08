import json
import os
import sys
import time
from dotenv import load_dotenv
import pika
import signal

from imageprocessor import process_file
from schemas import ImageSubmittedMsg
from db import get_session

# Always load .env from project root, one level above this file
env_path = os.path.abspath(os.path.join(os.path.dirname(__file__), "../.env"))
load_dotenv(env_path)


def consumer_connect():
    while True:
        try:
            connection = pika.BlockingConnection(
                pika.ConnectionParameters(host=os.getenv("RABBITMQ_HOST")),
            )
            channel = connection.channel()
            channel.queue_declare(
                queue=os.getenv("RABBITMQ_QUEUE_IMG_SUBMITTED"), durable=True
            )
            return connection, channel
        except pika.exceptions.AMQPConnectionError as e:
            print(f"Connection to RabbitMQ failed: {e}. Retrying in 5 seconds...")
            time.sleep(5)


connection, channel = consumer_connect()


def consume_messages(session):
    global connection, channel

    def callback(ch, method, properties, body):
        print(f" [x] Received {body.decode()}")
        msg = parse_msg_body_to_class(body)
        if msg:
            process_file(session, msg.file_name)
            # ch.basic_ack(delivery_tag=method.delivery_tag)

    while True:
        try:
            channel.basic_consume(
                queue=os.getenv("RABBITMQ_QUEUE_IMG_SUBMITTED"),
                on_message_callback=callback,
                auto_ack=True,
            )
            print(" [*] Waiting for messages")
            channel.start_consuming()
        except (
            pika.exceptions.AMQPConnectionError,
            pika.exceptions.StreamLostError,
        ) as e:
            print(f"Consumer connection lost: {e}. Reconnecting...")
            try:
                connection.close()
            except Exception:
                pass
            connection, channel = consumer_connect()


def parse_msg_body_to_class(body):
    try:
        if isinstance(body, bytes):
            body = body.decode()
        msg_dict = json.loads(body)
        msg = ImageSubmittedMsg(**msg_dict)
        return msg
    except json.JSONDecodeError:
        print("Error: Message body is not valid JSON.")
    except TypeError as e:
        print(f"Error: Missing or extra fields in message. Details: {e}")
    except Exception as e:
        print(f"Unexpected error: {e}")


def graceful_shutdown(signum, frame):
    print("Shutting down gracefully...")
    try:
        connection.close()
    except Exception:
        pass
    sys.exit(0)


signal.signal(signal.SIGTERM, graceful_shutdown)
signal.signal(signal.SIGINT, graceful_shutdown)

if __name__ == "__main__":
    for session in get_session():
        consume_messages(session)
