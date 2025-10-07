#!/usr/bin/env python3
"""
FastAPI Image Processor - Colados Leaderboard
A simple FastAPI application for processing Mario Kart images.
"""

from fastapi import FastAPI
from pydantic import BaseModel
import logging
from typing import List
from imageprocessor import get_results

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# Initialize FastAPI app
app = FastAPI(
    title="Colados Image Processor",
    description="API for processing Mario Kart leaderboard images",
    version="1.0.0",
)


class LeaderboardEntry(BaseModel):
    position: str
    player: str
    score: str


class ProcessingResult(BaseModel):
    image_name: str
    entries: List[LeaderboardEntry]
    total_entries: int


@app.get("/")
async def root():
    return get_results("mk8d-sb-10.jpg")


@app.get("/health")
async def health_check():
    """Health check endpoint."""
    return {"status": "healthy", "service": "mario-kart-processor"}


if __name__ == "__main__":
    import uvicorn

    # This allows running the file directly for debugging
    uvicorn.run("main:app", host="127.0.0.1", port=8000, reload=True, log_level="info")
