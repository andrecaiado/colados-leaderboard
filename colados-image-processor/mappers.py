import json
from schemas import ProcessedImage, ProcessedImageResponse, PlayerResult

def to_processed_image_response(processed_image: ProcessedImage) -> ProcessedImageResponse:
    # If results is a string, parse it
    results = processed_image.results
    if isinstance(results, str):
        results = json.loads(results)
    # Now results should be a list of dicts
    return ProcessedImageResponse(
        image_name=processed_image.image_name,
        processed_at=processed_image.processed_at.isoformat() if processed_image.processed_at else "",
        results=[PlayerResult(**item) for item in results or []]
    )