from datetime import datetime, timezone
from typing import Optional
from bson import ObjectId
from dotenv import load_dotenv
from schemas import ProcessedFile, Status
from db import processed_files

load_dotenv()

def get_processed_file(file_name: str) -> ProcessedFile | None:
    if file_name:
        doc = processed_files.find_one({"file_name": file_name})
    else:
        return None
    
    if doc:
        # Convert MongoDB’s _id to string if needed
        doc["id"] = str(doc["_id"])
        return ProcessedFile(**doc)
    return None

def store_processed_file(file_name: str, results: list[dict], status: Status):
    existing_doc = get_processed_file(file_name=file_name)

    if existing_doc:
        print(f"File {file_name} already processed. Updating record.")
        # Prevent downgrading status from PROCESSED to FAILED
        # if existing_doc.status == Status.PROCESSED.value and status == Status.FAILED:
        #     print(f"Existing status is PROCESSED. Not downgrading to FAILED.")
        #     return
        processed_files.update_one(
            {"file_name": file_name},
            {
                "$set": {
                    "results": results,
                    "processed_at": datetime.now(timezone.utc).isoformat(),
                    "status": status.value,
                }
            },
        )
        return
    
    doc = ProcessedFile(
        file_name=file_name,
        processed_at=datetime.now(timezone.utc).isoformat(),
        results=results,
        status=status.value,
    )
    processed_files.insert_one(doc.model_dump())
