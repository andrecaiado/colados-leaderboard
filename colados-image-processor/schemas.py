from datetime import datetime, timezone
from pydantic import BaseModel
from typing import Optional
from enum import Enum

class Status(Enum):
    PROCESSED = "processed"
    FAILED = "failed"

class ProcessedFile(BaseModel):
    id: Optional[str] = None  # MongoDB document ID
    file_name: str
    processed_at: str  # or datetime if you handle conversion
    results: list[dict] | dict  # Can be a list of player results or an exception dict
    status: Status

    class Config:
        use_enum_values = True

class FileSubmittedMsg(BaseModel):
    file_name: str


class FileProcessedMsg(BaseModel):
    file_name: str
    processed_at: str | None = datetime.now(timezone.utc).isoformat()
    results: list[dict] | dict  # Can be a list of player results or an exception dict
    status: Status

    class Config:
        use_enum_values = True
