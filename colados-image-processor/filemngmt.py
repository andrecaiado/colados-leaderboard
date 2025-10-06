import os
from minio import Minio
from dotenv import load_dotenv
import io

load_dotenv()

tmp_files_path = os.path.join(os.path.dirname(os.path.abspath(__file__)), 'tmp')
bucket_name = os.getenv("MINIO_BUCKET_NAME", "colados-image-processor")

minio_client = Minio(
    os.getenv("MINIO_ENDPOINT", "localhost:9000"),
    access_key=os.getenv("MINIO_ACCESS_KEY"),
    secret_key=os.getenv("MINIO_SECRET_KEY"),
    secure=os.getenv("MINIO_SECURE", "false").lower() == "true"
)

def download_file_from_bucket(object_name):
    file_path = os.path.join(tmp_files_path, object_name)
    try:
        minio_client.fget_object(bucket_name, object_name, file_path)
    except Exception as e:
        print(f"Error downloading file from {bucket_name}/{object_name}: {e}")

    return file_path

def delete_tmp_file(object_name):
    try:
        os.remove(os.path.join(tmp_files_path, object_name))
    except Exception as e:
        print(f"Error deleting tmp file from {tmp_files_path}/{object_name}: {e}")