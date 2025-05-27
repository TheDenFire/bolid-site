import requests
from bs4 import BeautifulSoup
import psycopg2
from datetime import datetime
import time
import logging

# Настройка логирования
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s'
)

# Конфигурация базы данных
DB_CONFIG = {
    'dbname': 'database_bolid',
    'user': 'testuser',
    'password': '123123',
    'host': 'localhost',
    'port': '5432'
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

def get_db_connection():
    """Создание подключения к базе данных"""
    try:
        return psycopg2.connect(**DB_CONFIG)
    except Exception as e:
        logging.error(f"Ошибка подключения к базе данных: {e}")
        return None

def save_news_to_db(conn, news_items):
    """Сохранение новостей в базу данных"""
    try:
        with conn.cursor() as cur:
            for news in news_items:
                # Проверяем, существует ли уже такая новость
                cur.execute("""
                    SELECT id FROM news 
                    WHERE (title = %s AND source_url = %s)
                    OR (source_url = %s AND published_at = %s)
                """, (news['title'], news['source_url'], news['source_url'], news['published_at']))
                
                if not cur.fetchone():
                    cur.execute("""
                        INSERT INTO news (title, summary, source_url, created_at, published_at)
                        VALUES (%s, %s, %s, %s, %s)
                    """, (
                        news['title'],
                        news['summary'],
                        news['source_url'],
                        datetime.now(),
                        news['published_at']
                    ))
            conn.commit()
            logging.info(f"Сохранено {len(news_items)} новых статей")
    except Exception as e:
        logging.error(f"Ошибка при сохранении в базу данных: {e}")
        conn.rollback()

def parse_news():
    """Парсинг новостей с различных источников"""
    all_news = []
    
    for source in NEWS_SOURCES:
        try:
            logging.info(f"Начинаем парсинг источника: {source['url']}")
            
            headers = {
                'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36',
                'Accept-Language': 'en-US,en;q=0.9',
                'Accept-Encoding': 'gzip, deflate, br',
                'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8',
                'Connection': 'keep-alive',
                'Referer': 'https://www.google.com/'
            }
            if 'autosport.com' in source['url']:
                with open('autosport.html', 'r', encoding='utf-8') as f:
                    html = f.read()
                response = type('obj', (object,), {'text': html, 'status_code': 200})()
            elif 'f1news.ru' in source['url']:
                try:
                    with open('f1news.html', 'r', encoding='utf-8') as f:
                        html = f.read()
                    response = type('obj', (object,), {'text': html, 'status_code': 200})()
                except FileNotFoundError:
                    response = requests.get(source['url'], headers=headers)
                    response.raise_for_status()
            else:
                response = requests.get(source['url'], headers=headers)
                response.raise_for_status()
            
            logging.info(f"Получен ответ от {source['url']}, статус: {response.status_code}")
            
            soup = BeautifulSoup(response.text, 'html.parser')
            
            if 'autosport.com' in source['url']:
                container = soup.select_one(source['container_selector'])
                if not container:
                    logging.warning('Контейнер новостей не найден!')
                    continue
                articles = container.select(source['item_selector'])
                logging.info(f"Найдено {len(articles)} статей на странице (autosport.com)")
                for article in articles:
                    try:
                        title_elem = article.select_one(source['title_selector'])
                        summary_elem = article.select_one(source['summary_selector'])
                        title = title_elem.get_text(strip=True) if title_elem else ''
                        summary = summary_elem.get_text(strip=True) if summary_elem else ''
                        link = article.get('href', '')
                        if not link.startswith('http'):
                            link = requests.compat.urljoin(source['url'], link)
                        if title:
                            all_news.append({
                                'title': title,
                                'summary': summary,
                                'source_url': link,
                                'published_at': datetime.now()  # Временное решение, пока нет парсинга даты
                            })
                            logging.info(f"Успешно обработана статья: {title}")
                    except Exception as e:
                        logging.error(f"Ошибка при парсинге статьи autosport: {e}")
                        continue
            elif 'f1news.ru' in source['url']:
                items = soup.select(source['item_selector'])
                logging.info(f"Найдено {len(items)} статей на странице (f1news.ru)")
                for item in items:
                    try:
                        title_elem = item.select_one(source['title_selector'])
                        date_elem = item.select_one(source['date_selector'])
                        summary_elem = item.select_one(source['summary_selector'])
                        
                        if title_elem:
                            # Удаляем дату из заголовка
                            title = title_elem.get_text(strip=True)
                            published_at = datetime.now()  # По умолчанию текущее время
                            
                            if date_elem:
                                date_text = date_elem.get_text(strip=True)
                                title = title.replace(date_text, '').strip()
                                try:
                                    # Пытаемся распарсить дату из текста
                                    published_at = datetime.strptime(date_text.strip(), '%d.%m.%Y')
                                except ValueError:
                                    pass  # Если не удалось распарсить, оставляем текущее время
                            
                            link = title_elem.get('href', '')
                            if not link.startswith('http'):
                                link = requests.compat.urljoin(source['url'], link)
                            
                            summary = summary_elem.get_text(strip=True) if summary_elem else ''
                            
                            all_news.append({
                                'title': title,
                                'summary': summary,
                                'source_url': link,
                                'published_at': published_at
                            })
                            logging.info(f"Успешно обработана статья: {title}")
                    except Exception as e:
                        logging.error(f"Ошибка при парсинге статьи f1news.ru: {e}")
                        continue
            else:
                articles = soup.select(source['title_selector'])
                logging.info(f"Найдено {len(articles)} статей на странице")
                for article in articles:
                    try:
                        title = article.get_text(strip=True)
                        parent = article.parent
                        while parent and not parent.select_one(source['summary_selector']):
                            parent = parent.parent
                        if parent:
                            summary_elem = parent.select_one(source['summary_selector'])
                            link_elem = parent.select_one(source['link_selector'])
                            if summary_elem and link_elem:
                                summary = summary_elem.get_text(strip=True)
                                link = link_elem['href']
                                if not link.startswith('http'):
                                    link = requests.compat.urljoin(source['url'], link)
                                all_news.append({
                                    'title': title,
                                    'summary': summary,
                                    'source_url': link,
                                    'published_at': datetime.now()  # Временное решение, пока нет парсинга даты
                                })
                                logging.info(f"Успешно обработана статья: {title}")
                            else:
                                logging.warning(f"Не найдены summary или link для статьи: {title}")
                        else:
                            logging.warning(f"Не найден родительский элемент для статьи: {title}")
                    except Exception as e:
                        logging.error(f"Ошибка при парсинге статьи: {e}")
                        continue
            logging.info(f"Успешно обработан источник: {source['url']}")
        except Exception as e:
            logging.error(f"Ошибка при обработке источника {source['url']}: {e}")
            continue
    return all_news

def main():
    """Основная функция"""
    while True:
        try:
            # Получаем новости
            news_items = parse_news()
            
            # Сохраняем в базу данных
            conn = get_db_connection()
            if conn:
                save_news_to_db(conn, news_items)
                conn.close()
            
            # Ждем час перед следующей проверкой
            time.sleep(3600)
            
        except Exception as e:
            logging.error(f"Произошла ошибка: {e}")
            time.sleep(300)  # Ждем 5 минут перед повторной попыткой

if __name__ == "__main__":
    main() 