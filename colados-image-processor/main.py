#!/usr/bin/env python3
"""
FastAPI Image Processor - Colados Leaderboard
A simple FastAPI application for processing Mario Kart images.
"""

from contextlib import asynccontextmanager
from typing_extensions import Annotated
from fastapi import FastAPI
import logging
from sqlmodel import SQLModel, Session
from repository import get_processed_image_by_name
from db import db_engine

from fastapi.params import Depends
from db import get_session
from msgconsumer import consumer_connect
from http import HTTPStatus as HttpStatus

# Initialize FastAPI app
app = FastAPI(
    title="Colados Image Processor",
    description="Service for processing Mario Kart scoreboard images",
    version="1.0.0",
)


@asynccontextmanager
async def lifespan(app: FastAPI):
    SQLModel.metadata.create_all(db_engine)
    yield


# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


@app.get("/process/{image_name}", status_code=HttpStatus.OK)
def get_processed_image(
    session: Annotated[Session, Depends(get_session)], image_name: str
):
    result = get_processed_image_by_name(session, image_name)
    if result:
        return result
    return {"error": "Processed image not found"}, HttpStatus.NOT_FOUND


if __name__ == "__main__":
    import uvicorn

    # This allows running the file directly for debugging
    uvicorn.run("main:app", host="127.0.0.1", port=8000, reload=True, log_level="info")
