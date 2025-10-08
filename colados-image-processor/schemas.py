from datetime import datetime, timezone
from pydantic import BaseModel
from sqlmodel import Column, Field, SQLModel

from sqlalchemy import JSON


class ProcessedImage(SQLModel, table=True):
    __tablename__ = "processed_images"
    id: int | None = Field(default=None, primary_key=True)
    image_name: str
    processed_at: datetime | None = Field(
        default_factory=lambda: datetime.now(timezone.utc)
    )
    results: list = Field(sa_column=Column(JSON))


class ImageSubmittedMsg(BaseModel):
    file_name: str


class ImageProcessedMsg(BaseModel):
    file_name: str
    processed_at: str | None = datetime.now(timezone.utc).isoformat()
    results: dict


class PlayerResult(BaseModel):
    position: int | None = None
    name: str | None = None
    score: int | None = None

class ProcessedImageResponse(BaseModel):
    image_name: str
    processed_at: str
    results: list[PlayerResult]
