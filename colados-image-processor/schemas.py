from datetime import datetime, timezone
from pydantic import BaseModel
from typing import Optional
from enum import Enum

class Status(Enum):
    PROCESSED = "processed"
    FAILED = "failed"
    NONE = "none"

class ProcessedFileDetails(BaseModel):
    id: Optional[str] = None 
    file_name: str
    processed_at: datetime
    results: list[dict]
    status: Status

    class Config:
        use_enum_values = True

class FileSubmittedMsg(BaseModel):
    file_name: str


class FileProcessedMsg(BaseModel):
    file_name: str
    results: list[dict]
    status: Status

    class Config:
        use_enum_values = True
