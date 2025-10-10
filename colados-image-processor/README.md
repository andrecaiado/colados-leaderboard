# Colados Image Processor

## Overview

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
4. Set up environment variables for Roboflow, MongoDB, and MongoExpress services (see `.env` file for reference).
5. Make sure you have RabbitMQ service running (you can use the docker compose setup in the parent repository and start the RabbitMQ service).
```shell
docker compose up -d rabbitmq mongodb mongo-express
```
6. Start the FastAPI application.
```shell
uvicorn main:app --reload
```
7. Start the messages consumer.
```shell
python msgconsumer.py
```