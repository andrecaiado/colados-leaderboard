# Colados Image Processor

This project is part of the Colados Leaderboard project. It processes Mario Kart 8 Deluxe scoreboard images to extract player results and stores them in a database.
The image analysis and data extraction is performed by a Roboflow model deployed on their platform.

## How It Works
1. Consumes messages from a RabbitMQ queue that announces new images to process.
2. Fetches images from a storage service (MinIO).
3. Submits images to the Roboflow model for analysis.
4. Extracts player results from the model's response.
5. Stores processed data in a PostgreSQL database.
6. Publishes processed results to a RabbitMQ message queue.

### Additional Features
- Exposes an endpoint to get processed image results in JSON format.

## How to Run
> **Note:** 
The following instructions are for running this project only. To run the complete Colados Leaderboard system, you can use the docker compose setup in the parent repository.

1. Clone the repository.
```shell
git clone <repository-url>
cd colados-image-processor
```
2. Create a virtual environment for this project (optional but recommended).
```shell
python -m venv venv
source venv/bin/activate  # On Windows use `venv\Scripts\activate`
```
3. Install dependencies.
```shell
pip install -r requirements.txt
```
4. Set up environment variables in the [.env](../.env) file.
5. Start the services using Docker Compose.
```shell
docker compose up -d
```
6. Start the FastAPI application.
```shell
uvicorn main:app --reload
```
7. Start the messages consumer.
```shell
python msgconsumer.py
```

## How to test

### Upload test images to MinIO
Upload test images to the MinIO bucket specified in the `.env` file (e.g., using the MinIO web UI).

### Send test messages to RabbitMQ
Send test messages to the RabbitMQ exchange `image_submitted_exchange` to simulate new images for processing (e.g., by using the Rabbit MQ web UI).

You must submit a routing key that matches the one specified in the `.env` file (default is `image_submitted_key`).
The message format should be as follows:
```json
{
  "file_name": "your-file-name.jpg"
}
```

### Verify the processing results
You can verify the processing results in different ways:
- Check the MongoDB database for stored results (in the `processed_files` collection).
- Check the RabbitMQ `image_processed_queue` queue for published processed results.
- Check the FastAPI endpoint for processed results (through a browser or executing the following curl command):
```shell
curl -X GET "http://localhost:8000/processedfile/your-file-name.jpg"
```