#!/usr/bin/env python3
"""
FastAPI Image Processor - Colados Leaderboard
A simple FastAPI application for processing images.
"""

from fastapi import FastAPI, HTTPException, UploadFile, File
from fastapi.responses import JSONResponse
from pydantic import BaseModel
from typing import List, Dict, Any, Optional
import os
import logging

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# Initialize FastAPI app
app = FastAPI(
    title="Colados Image Processor",
    description="API for processing images for the Colados leaderboard",
    version="1.0.0"
)

@app.get("/")
async def root():
    """Root endpoint returning API information."""
    return {
        "message": "Colados Image Processor API",
        "version": "1.0.0",
        "status": "active"
    }


@app.get("/health")
async def health_check():
    """Health check endpoint."""
    return {"status": "healthy", "service": "image-processor"}

if __name__ == "__main__":
    import uvicorn
    
    # This allows running the file directly for debugging
    uvicorn.run(
        "main:app",
        host="127.0.0.1",
        port=8000,
        reload=True,
        log_level="info"
    )