import os
from dotenv import load_dotenv
from pymongo import MongoClient

env_path = os.path.abspath(os.path.join(os.path.dirname(__file__), "../.env"))
load_dotenv(env_path)

def get_mongo_client():
    mongo_host = os.getenv("MONGODB_HOST", "localhost")
    mongo_port = os.getenv("MONGODB_PORT", "27017")
    mongo_user = os.getenv("MONGO_INITDB_ROOT_USERNAME")
    mongo_password = os.getenv("MONGO_INITDB_ROOT_PASSWORD")
    mongo_uri = f"mongodb://{mongo_user}:{mongo_password}@{mongo_host}:{mongo_port}/admin"
    client = MongoClient(mongo_uri)
    return client

# Create a MongoDB client
client = get_mongo_client()

# Access a database
db = client["colados_image_processor_db"]

# Access a collection
processed_images = db["processed_images"]
    