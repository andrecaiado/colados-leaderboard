from datetime import datetime, timezone
from pydantic import BaseModel
from typing import Optional

class ProcessedImage(BaseModel):
    id: Optional[str] = None  # MongoDB document ID
    image_name: str
    processed_at: str  # or datetime if you handle conversion
    results: list[dict] 

class ImageSubmittedMsg(BaseModel):
    file_name: str


class ImageProcessedMsg(BaseModel):
    file_name: str
    processed_at: str | None = datetime.now(timezone.utc).isoformat()
    results: dict
