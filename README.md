# java-filmorate

В этом спринте вы начнёте с малого, но очень важного: 
создадите каркас Spring Boot приложения Filmorate (от англ. film — «фильм» и rate — «оценивать»). 
В дальнейшем сервис будет обогащаться новым функционалом и с каждым спринтом становиться лучше благодаря вашим знаниям о Java. 
Скорее вперёд!

В проекте реализованы: 

<details>
    <summary><h3> Спринт 9:</h3></summary>
Выполнено проектирование согласно Техническому заданию:
1. Определены модели данных приложения: Film, User;
2. Организовано предварительной хранение данных;
3. Созданы REST-контроллеры: FilmController, UserController;
3. Задана валидация данных;
4. Выполнено логирование данных;
5. Валидация проверяется тестами Unit5.
</details>

<details>
    <summary><h3> Спринт 10:</h3></summary>
1. Переработана архитектура проекта:
* созданы интерфейсы FilmStorage и UserStorage; 
* созданы классы InMemoryFilmStorage и InMemoryUserStorage (@Component); 
* созданы классы UserService и FilmService (@Service);
2. API доведен до соответствия REST;
3. Настроен ExceptionHandler для централизованной обработки ошибок
</details>

# * Спринт 11. Часть 1:
1. Переработана архитектура проекта: добавлены дополнительные поля в классы Film (жанр и рейтинг) и User (список дружбы).
 
2. Спроектирована схема БД:
   ![ER-диаграмма](/images/FILMORATE_DB.png)
* Связь между users (пользователи) и friends (таблица сопоставления друзей) "many to many";
* Связь между users и films  "many to many" через табличку likes;
* Связь между film и genres  "many to many" через табличку filmgenres; 
* Связь между mpa и film "one to many" т.к. mpaid уникален и может быть присвоен множеству фильмов;
* Для таблиц friends, likes и filmgenres использованы составные Primary Key из двух id.

3. ### Примеры запросов

<details>
    <summary><h3>Работа с фильмами:</h3></summary>

* Запрос фильма по id:

```SQL
SELECT f.name,
       f.description,
       f.releaseDate,
       f.duration,
       m.name,
       g.name
FROM films f
JOIN mpa m ON f.mpaid = m.id
JOIN filmgenres fg ON f.id = fg.filmid
JOIN genres g ON fg.genreid = g.id
WHERE f.id = ?;
```   

* Запрос всех фильмов:

```SQL
SELECT f.name,
       f.description,
       f.releaseDate,
       f.duration,
       m.name,
       g.name
FROM films f
JOIN mpa m ON f.mpaid = m.id
JOIN filmgenres fg ON f.id = fg.filmid
JOIN genres g ON fg.genreid = g.id;
```

* Запрос топ-N фильмов по количеству лайков:
```SQL
SELECT f.name,
       COUNT(l.filmid) AS likes_count
FROM films f
JOIN likes l ON f.id = l.filmid
GROUP BY f.name
ORDER BY likes_count DESC
LIMIT N;
```
</details>

<details>
    <summary><h3>Работа с пользователями:</h3></summary>

* Запрос пользователя по id:

```SQL
SELECT *
FROM users
WHERE id = ?;
```   

* Запрос всех пользователей:

```SQL
SELECT *
FROM users;
``` 

</details>

<details>
    <summary><h3>Работа с жанрами:</h3></summary>

* Запрос жанра по id:

```SQL
SELECT *
FROM genres
WHERE id = ?;
``` 

* Запрос всех жанров:

```SQL
SELECT *
FROM genres;
```   
</details>

<details>
    <summary><h3>Работа с рейтингами MPA:</h3></summary>

* Запрос рейтинга по id:

```SQL
SELECT *
FROM mpa
WHERE id = ?;
``` 

* Запрос всех рейтингов MPA:

```SQL
SELECT *
FROM mpa;
```   
</details>

## Инструкция по установке

- [Требования](#требования)
- [Установка](#установка)
- [Запуск](#запуск)

### Требования

- Apache Maven 3.6.0 и позднее
- JDK 11 и позднее

### Установка

1. Клонировать репозиторий:
```bash
git clone https://github.com/AKnazzz/java-filmorate.git
```

2. Перейти в корневую директорию проекта:
```bash
cd java-filmorate
```

3. Собрать проект, используя Maven:
```bash
mvn clean install
```

### Запуск

После установки запустить приложение:
```bash
mvn spring-boot:run
```
