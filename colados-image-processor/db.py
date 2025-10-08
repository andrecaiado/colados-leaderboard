import os
from dotenv import load_dotenv
from sqlmodel import Session, create_engine

env_path = os.path.abspath(os.path.join(os.path.dirname(__file__), "../.env"))
load_dotenv(env_path)


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


def get_session():
    with Session(db_engine) as session:
        yield session
