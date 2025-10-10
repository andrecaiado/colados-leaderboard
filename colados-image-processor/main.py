#!/usr/bin/env python3
from fastapi import FastAPI
import logging
from repository import get_processed_file

from http import HTTPStatus as HttpStatus

# Initialize FastAPI app
app = FastAPI(
    title="Colados Image Processor",
    description="Service for processing Mario Kart scoreboard images",
    version="1.0.0",
)

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


@app.get("/processedfile/{file_id}", status_code=HttpStatus.OK)
def get_processedfile(
    file_id: str
):
    result = get_processed_file(id=file_id)
    if result:
        return result
    return {"error": "Processed file not found"}, HttpStatus.NOT_FOUND


if __name__ == "__main__":
    import uvicorn

    # This allows running the file directly for debugging
    uvicorn.run("main:app", host="127.0.0.1", port=8000, reload=True, log_level="info")
