import os
from dotenv import load_dotenv

# Загрузка переменных окружения из config.env
load_dotenv('config.env')

# Конфигурация базы данных
DB_CONFIG = {
    'dbname': os.getenv('DB_NAME'),
    'user': os.getenv('DB_USER'),
    'password': os.getenv('DB_PASSWORD'),
    'host': os.getenv('DB_HOST'),
    'port': os.getenv('DB_PORT')
}

# Проверка наличия всех необходимых переменных окружения
required_env_vars = ['DB_NAME', 'DB_USER', 'DB_PASSWORD', 'DB_HOST', 'DB_PORT']
missing_vars = [var for var in required_env_vars if not os.getenv(var)]
if missing_vars:
    raise ValueError(f"Missing required environment variables: {', '.join(missing_vars)}")

# Настройки парсера
PARSER_CONFIG = {
    'interval': int(os.getenv('PARSER_INTERVAL', 3600)),  # Интервал между проверками в секундах
    'retry_interval': 300,  # Интервал между повторными попытками при ошибке
    'user_agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36'
}

# Список новостных источников
NEWS_SOURCES = [
    {
        'url': 'https://www.formula1.com/en/latest/all.html',
        'title_selector': '.f1-article-title',
        'summary_selector': '.f1-article-excerpt',
        'link_selector': '.f1-article-link'
    },
    {
        'url': 'https://www.autosport.com/f1/news/',
        'container_selector': '.ms-grid',
        'item_selector': 'a',
        'title_selector': '.ms-item__title',
        'summary_selector': '.ms-item__thumb-title'
    },
    {
        'url': 'https://www.f1news.ru/news/',
        'item_selector': '.b-article__item',
        'title_selector': '.article_title a',
        'date_selector': '.article_title a span',
        'summary_selector': '.article_content',
        'link_selector': '.article_title a'
    }
]

# Настройки логирования
LOGGING_CONFIG = {
    'level': 'INFO',
    'format': '%(asctime)s - %(levelname)s - %(message)s'
} 