from datetime import datetime, timezone
from dataclasses import asdict
from dotenv import load_dotenv
from requests import Session
from sqlalchemy import Column, DateTime, Integer, String, create_engine
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker
from sqlalchemy.dialects.postgresql import JSONB
import os

load_dotenv()

Base = declarative_base()
class ProcessedImages(Base):
    __tablename__ = 'processed_images'

    id = Column(Integer, primary_key=True)
    image_name = Column(String, nullable=False)
    processed_at = Column(DateTime, default=datetime.now(timezone.utc))
    results = Column(JSONB, nullable=False)

def get_database_engine():
    db_user = os.getenv("POSTGRES_USER", "colados_user")
    db_password = os.getenv("POSTGRES_PASSWORD", "colados_password")
    db_host = os.getenv("POSTGRES_HOST", "localhost")
    db_port = os.getenv("POSTGRES_PORT", "5432")
    db_name = os.getenv("POSTGRES_DB", "colados_image_processor_db")

    database_url = f"postgresql://{db_user}:{db_password}@{db_host}:{db_port}/{db_name}"
    engine = create_engine(database_url)
    return engine

db_engine = get_database_engine()

Base.metadata.create_all(db_engine)
Session = sessionmaker(bind=db_engine)
session = Session()

def to_dict(obj):
    if hasattr(obj, '__dict__'):
        return dict(obj.__dict__)
    return obj

def add_processed_image(image_name, results):
    if isinstance(results, list):
        results_to_save = [to_dict(r) for r in results]
    else:
        results_to_save = results

    record = get_processed_image_by_name(image_name)
    if record:
        print(f"Image {image_name} already processed. Updating record.")
        record.results = results_to_save
        record.processed_at = datetime.now(timezone.utc)
        session.commit()
        return
    
    new_image = ProcessedImages(image_name=image_name, results=results_to_save)
    session.add(new_image)
    session.commit()

def get_processed_image_by_name(image_name):
    return session.query(ProcessedImages).filter_by(image_name=image_name).first()