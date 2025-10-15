from datetime import datetime, timezone
from dotenv import load_dotenv
from schemas import ProcessedFileDetails, Status
from db import processed_files

load_dotenv()

def get_processed_details(file_name: str) -> ProcessedFileDetails | None:
    if file_name:
        doc = processed_files.find_one({"file_name": file_name})
    else:
        return None
    
    if doc:
        # Convert MongoDB’s _id to string if needed
        doc["id"] = str(doc["_id"])
        return ProcessedFileDetails(**doc)
    return None

def save_processed_file_details(file_name: str, results: list[dict], status: Status):
    existing_doc = get_processed_details(file_name=file_name)
    
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
                    "processed_at": datetime.now(timezone.utc),
                    "status": status.value,
                }
            },
        )
        return
    
    doc = ProcessedFileDetails(
        file_name=file_name,
        processed_at=datetime.now(timezone.utc),
        results=results,
        status=status.value,
    )
    processed_files.insert_one(doc.model_dump())
