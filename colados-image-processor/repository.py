from datetime import datetime, timezone
from typing import Annotated
from dotenv import load_dotenv
from fastapi.params import Depends
from sqlmodel import Session, select
from db import get_session
from schemas import ProcessedImage

load_dotenv()


def to_dict(obj):
    if hasattr(obj, "__dict__"):
        return dict(obj.__dict__)
    return obj


def store_processed_image_results(session, image_name, results):
    if isinstance(results, list):
        results_to_save = [to_dict(r) for r in results]
    else:
        results_to_save = results

    record = get_processed_image_by_name(session, image_name)
    if record:
        print(f"Image {image_name} already processed. Updating record.")
        record.results = results_to_save
        record.processed_at = datetime.now(timezone.utc)
        session.commit()
        return

    new_image = ProcessedImage(image_name=image_name, results=results_to_save)
    session.add(new_image)
    session.commit()


def get_processed_image_by_name(session, image_name) -> ProcessedImage | None:
    query = select(ProcessedImage).where(ProcessedImage.image_name == image_name)
    result = session.exec(query).first()
    return result
