from dataclasses import dataclass
import json
import os
import sys
from dotenv import load_dotenv
import pika
import signal
from imageprocessor import process_file

load_dotenv()


@dataclass
class ImageSubmittedMessage:
    file_name: str


connection = pika.BlockingConnection(
    pika.ConnectionParameters(host=os.getenv("RABBITMQ_HOST")),
)
channel = connection.channel()
channel.queue_declare(queue=os.getenv("RABBITMQ_QUEUE_IMG_SUBMITTED"), durable=True)


def consume_messages():
    def callback(ch, method, properties, body):
        print(f" [x] Received {body.decode()}")
        msg = map_message_to_class(body)
        if msg:
            process_file(msg.file_name)
            # ch.basic_ack(delivery_tag=method.delivery_tag)

    channel.basic_consume(
        queue=os.getenv("RABBITMQ_QUEUE_IMG_SUBMITTED"),
        on_message_callback=callback,
        auto_ack=True,
    )

    print(" [*] Waiting for messages")
    channel.start_consuming()


def map_message_to_class(body):
    try:
        if isinstance(body, bytes):
            body = body.decode()
        msg_dict = json.loads(body)
        msg = ImageSubmittedMessage(**msg_dict)
        print(msg.file_name)
        return msg
    except json.JSONDecodeError:
        print("Error: Message body is not valid JSON.")
    except TypeError as e:
        print(f"Error: Missing or extra fields in message. Details: {e}")
    except Exception as e:
        print(f"Unexpected error: {e}")

    except json.JSONDecodeError:
        print("Error: Message body is not valid JSON.")
    except TypeError as e:
        print(f"Error: Missing or extra fields in message. Details: {e}")
    except Exception as e:
        print(f"Unexpected error: {e}")


def graceful_shutdown(signum, frame):
    print("Shutting down gracefully...")
    connection.close()
    sys.exit(0)


signal.signal(signal.SIGTERM, graceful_shutdown)
signal.signal(signal.SIGINT, graceful_shutdown)

if __name__ == "__main__":
    consume_messages()
