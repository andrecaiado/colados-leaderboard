from datetime import datetime, timezone
from typing import Optional
from bson import ObjectId
from dotenv import load_dotenv
from schemas import ProcessedImage
from db import processed_images

load_dotenv()

def get_processed_image(id: Optional[str] = None, image_name: Optional[str] = None) -> ProcessedImage | None:
    if id:
        doc = processed_images.find_one({"_id": ObjectId(id)})
    elif image_name:
        doc = processed_images.find_one({"image_name": image_name})
    else:
        return None
    
    if doc:
        # Convert MongoDB’s _id to string if needed
        doc["id"] = str(doc["_id"])
        return ProcessedImage(**doc)
    return None

def store_processed_image(image_name: str, results: list[dict]):
    existing_doc = get_processed_image(image_name=image_name)

    if existing_doc:
        print(f"Image {image_name} already processed. Updating record.")
        processed_images.update_one(
            {"image_name": image_name},
            {
                "$set": {
                    "results": results,
                    "processed_at": datetime.now(timezone.utc).isoformat(),
                }
            },
        )
        return
    
    doc = ProcessedImage(
        image_name=image_name,
        processed_at=datetime.now(timezone.utc).isoformat(),
        results=results,
    )
    processed_images.insert_one(doc)
