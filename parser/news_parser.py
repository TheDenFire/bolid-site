import requests
from bs4 import BeautifulSoup
import psycopg2
from datetime import datetime
import time
import logging
from config import DB_CONFIG, PARSER_CONFIG, NEWS_SOURCES, LOGGING_CONFIG
import re

# Настройка логирования
logging.basicConfig(
    level=getattr(logging, LOGGING_CONFIG['level']),
    format=LOGGING_CONFIG['format']
)

def parse_date(date_elem, title=None, source_url=None):
    """Парсинг даты из элемента с атрибутом datePublished или из заголовка"""
    # Для f1news.ru пробуем получить дату из заголовка
    if source_url and 'f1news.ru' in source_url and title:
        try:
            # Ищем дату в формате "DD месяца, HH:MM" в начале заголовка
            date_match = re.search(r'^(\d{1,2})\s+([а-яА-ЯёЁ]+)\s*,\s*(\d{1,2}):(\d{2})', title)
            if date_match:
                day = date_match.group(1)
                month_ru = date_match.group(2).lower()
                hour = date_match.group(3)
                minute = date_match.group(4)
                
                # Преобразуем русские названия месяцев в числа
                months = {
                    'января': '01', 'февраля': '02', 'марта': '03', 'апреля': '04',
                    'мая': '05', 'июня': '06', 'июля': '07', 'августа': '08',
                    'сентября': '09', 'октября': '10', 'ноября': '11', 'декабря': '12'
                }
                
                if month_ru in months:
                    month = months[month_ru]
                    current_year = datetime.now().year
                    # Формируем строку даты в формате YYYY-MM-DD HH:MM
                    date_str = f"{current_year}-{month}-{day:0>2} {hour}:{minute}:00"
                    parsed_date = datetime.strptime(date_str, '%Y-%m-%d %H:%M:%S')
                    logging.info(f"Распарсена дата из заголовка: {parsed_date}")
                    return parsed_date
        except Exception as e:
            logging.warning(f"Не удалось распарсить дату из заголовка: {e}")
    
    # Если не f1news.ru или не удалось получить дату из заголовка, пробуем из элемента
    if date_elem:
        try:
            # Пробуем получить дату из атрибута content
            date_str = date_elem.get('content', '')
            if date_str:
                # Удаляем часовой пояс из строки даты
                date_str = date_str.split('+')[0]
                parsed_date = datetime.strptime(date_str, '%Y-%m-%dT%H:%M:%S')
                logging.info(f"Распарсена дата из атрибута content: {parsed_date}")
                return parsed_date
        except (ValueError, AttributeError) as e:
            logging.warning(f"Не удалось распарсить дату из атрибута content: {e}")
        
        try:
            # Если не удалось получить из атрибута, пробуем из текста
            date_text = date_elem.get_text(strip=True)
            # Пробуем разные форматы даты
            formats = [
                '%d %B %Y, %H:%M',  # 26 мая 2025, 13:27
                '%d.%m.%Y %H:%M',   # 26.05.2025 13:27
                '%Y-%m-%d %H:%M:%S' # 2025-05-26 13:27:00
            ]
            
            for fmt in formats:
                try:
                    parsed_date = datetime.strptime(date_text, fmt)
                    logging.info(f"Распарсена дата из текста: {parsed_date}")
                    return parsed_date
                except ValueError:
                    continue
        except Exception as e:
            logging.warning(f"Не удалось распарсить дату из текста: {e}")
    
    logging.warning("Не удалось распарсить дату ни из одного источника")
    return None

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
                # Логируем значения для отладки
                logging.info(f"Сохраняем новость: {news['title']}")
                logging.info(f"original_published_at: {news.get('original_published_at')}")
                
                # Проверяем, существует ли уже такая новость
                cur.execute("""
                    SELECT id FROM news 
                    WHERE (title = %s AND source_url = %s)
                    OR (source_url = %s AND published_at = %s)
                """, (news['title'], news['source_url'], news['source_url'], news['published_at']))
                
                if not cur.fetchone():
                    # Преобразуем datetime в строку для SQL
                    original_published_at = news.get('original_published_at')
                    if original_published_at:
                        original_published_at = original_published_at.strftime('%Y-%m-%d %H:%M:%S')
                    
                    cur.execute("""
                        INSERT INTO news (title, summary, source_url, image_url, published_at, original_published_at)
                        VALUES (%s, %s, %s, %s, %s, %s)
                    """, (
                        news['title'],
                        news['summary'],
                        news['source_url'],
                        news.get('image_url'),
                        news['published_at'].strftime('%Y-%m-%d %H:%M:%S'),
                        original_published_at
                    ))
                    logging.info(f"Новость сохранена с original_published_at: {original_published_at}")
            conn.commit()
            logging.info(f"Сохранено {len(news_items)} новых статей")
    except Exception as e:
        logging.error(f"Ошибка при сохранении в базу данных: {e}")
        conn.rollback()

def get_full_size_image_url(url):
    """Получение URL изображения в полном размере"""
    if not url:
        return url
    
    # Добавляем https: если URL начинается с //
    if url.startswith('//'):
        url = 'https:' + url
    
    # Удаляем параметры размера из URL
    if '?' in url:
        url = url.split('?')[0]
    
    # Для f1news.ru заменяем превью на полное изображение
    if 'f1news.ru' in url:
        # Заменяем i.f1news.ru на c.f1news.ru для получения полного изображения
        url = url.replace('i.f1news.ru', 'c.f1news.ru')
        # Удаляем путь /im/c/ и размеры
        url = re.sub(r'/im/c/\d+x\d+/', '/', url)
    
    return url

def parse_news():
    """Парсинг новостей с различных источников"""
    all_news = []
    
    for source in NEWS_SOURCES:
        try:
            logging.info(f"Начинаем парсинг источника: {source['url']}")
            
            headers = {
                'User-Agent': PARSER_CONFIG['user_agent'],
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
                        date_elem = article.select_one('[itemprop="datePublished"]')
                        
                        if title_elem:
                            title = title_elem.get_text(strip=True)
                            # Сохраняем оригинальный заголовок для парсинга даты
                            original_title = title
                            # Удаляем дату из заголовка для f1news.ru
                            if 'f1news.ru' in source['url']:
                                # Ищем дату в формате "DD месяца, HH:MM" в начале заголовка
                                title = re.sub(r'^\d{1,2}\s+[а-яА-ЯёЁ]+\s*,\s*\d{1,2}:\d{2}', '', title).strip()
                            
                            published_at = datetime.now()
                            original_published_at = parse_date(date_elem, original_title, source['url'])
                            logging.info(f"Распарсена дата для статьи '{title}': {original_published_at}")
                            
                            link = title_elem.get('href', '')
                            if not link.startswith('http'):
                                link = requests.compat.urljoin(source['url'], link)
                            
                            summary = summary_elem.get_text(strip=True) if summary_elem else ''
                            
                            # Для f1news.ru берем изображение из атрибута src
                            if 'f1news.ru' in source['url']:
                                image_elem = article.select_one('img[itemprop="contentUrl"]')
                                if not image_elem:
                                    image_elem = article.select_one('img[src*="f1news.ru"]')
                            else:
                                image_elem = article.select_one('img')
                                
                            image_url = image_elem.get('src', '') if image_elem else ''
                            if image_url and not image_url.startswith('http'):
                                image_url = requests.compat.urljoin(source['url'], image_url)
                            image_url = get_full_size_image_url(image_url)
                            
                            all_news.append({
                                'title': title,
                                'summary': summary,
                                'source_url': link,
                                'image_url': image_url,
                                'published_at': published_at,
                                'original_published_at': original_published_at
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
                        date_elem = item.select_one('[itemprop="datePublished"]')
                        summary_elem = item.select_one(source['summary_selector'])
                        
                        if title_elem:
                            title = title_elem.get_text(strip=True)
                            # Сохраняем оригинальный заголовок для парсинга даты
                            original_title = title
                            # Удаляем дату из заголовка для f1news.ru
                            if 'f1news.ru' in source['url']:
                                # Ищем дату в формате "DD месяца, HH:MM" в начале заголовка
                                title = re.sub(r'^\d{1,2}\s+[а-яА-ЯёЁ]+\s*,\s*\d{1,2}:\d{2}', '', title).strip()
                            
                            published_at = datetime.now()
                            original_published_at = parse_date(date_elem, original_title, source['url'])
                            logging.info(f"Распарсена дата для статьи '{title}': {original_published_at}")
                            
                            link = title_elem.get('href', '')
                            if not link.startswith('http'):
                                link = requests.compat.urljoin(source['url'], link)
                            
                            summary = summary_elem.get_text(strip=True) if summary_elem else ''
                            
                            # Для f1news.ru берем изображение из атрибута src
                            if 'f1news.ru' in source['url']:
                                image_elem = item.select_one('img[itemprop="contentUrl"]')
                                if not image_elem:
                                    image_elem = item.select_one('img[src*="f1news.ru"]')
                            else:
                                image_elem = item.select_one('img')
                                
                            image_url = image_elem.get('src', '') if image_elem else ''
                            if image_url and not image_url.startswith('http'):
                                image_url = requests.compat.urljoin(source['url'], image_url)
                            image_url = get_full_size_image_url(image_url)
                            
                            all_news.append({
                                'title': title,
                                'summary': summary,
                                'source_url': link,
                                'image_url': image_url,
                                'published_at': published_at,
                                'original_published_at': original_published_at
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
                            date_elem = parent.select_one('[itemprop="datePublished"]')
                            
                            if summary_elem and link_elem:
                                # Сохраняем оригинальный заголовок для парсинга даты
                                original_title = title
                                # Удаляем дату из заголовка для f1news.ru
                                if 'f1news.ru' in source['url']:
                                    # Ищем дату в формате "DD месяца, HH:MM" в начале заголовка
                                    title = re.sub(r'^\d{1,2}\s+[а-яА-ЯёЁ]+\s*,\s*\d{1,2}:\d{2}', '', title).strip()
                                
                                summary = summary_elem.get_text(strip=True)
                                link = link_elem['href']
                                if not link.startswith('http'):
                                    link = requests.compat.urljoin(source['url'], link)
                                    
                                # Для f1news.ru берем изображение из атрибута src
                                if 'f1news.ru' in source['url']:
                                    image_elem = parent.select_one('img[itemprop="contentUrl"]')
                                    if not image_elem:
                                        image_elem = parent.select_one('img[src*="f1news.ru"]')
                                else:
                                    image_elem = parent.select_one('img')
                                    
                                image_url = image_elem.get('src', '') if image_elem else ''
                                if image_url and not image_url.startswith('http'):
                                    image_url = requests.compat.urljoin(source['url'], image_url)
                                image_url = get_full_size_image_url(image_url)
                                    
                                published_at = datetime.now()
                                original_published_at = parse_date(date_elem, original_title, source['url'])
                                logging.info(f"Распарсена дата для статьи '{title}': {original_published_at}")
                                
                                all_news.append({
                                    'title': title,
                                    'summary': summary,
                                    'source_url': link,
                                    'image_url': image_url,
                                    'published_at': published_at,
                                    'original_published_at': original_published_at
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
            
            # Ждем указанное время перед следующей проверкой
            time.sleep(PARSER_CONFIG['interval'])
            
        except Exception as e:
            logging.error(f"Произошла ошибка: {e}")
            time.sleep(PARSER_CONFIG['retry_interval'])  # Ждем указанное время перед повторной попыткой

if __name__ == "__main__":
    main() 