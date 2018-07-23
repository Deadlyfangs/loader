Для запуска приложения необходимо:

1) Развернуть базу PostgresSQL 9.0+ со схемой public.
После этого, открыть конфигурационный файл в исходниках проекта fileloader/src/main/resources/application.yml.
Прописать параметры подключения:
        url: "jdbc:postgresql://ADDRESS:PORT/DBNAME"
        username: "USERNAME"
        password: "PASSWORD"

2) Запустить SQL-скрипт fileloader/src/main/resources/sql/schema.sql для создания схемы в БД (таблица для контента).

***Альтернативно 1 и 2***
Развернуть Docker 18+. Запустить файл docker-compose.yml командой docker-compose up.


3) Запустить приложение, например:
    java -jar fileloader-1.0.RELEASE.jar --scheduler.directory.source=/Users/SOME_USER_NAME/BanzaiFolder/New
    --scheduler.directory.processed=/Users/SOME_USER_NAME/BanzaiFolder/Old 
    --scheduler.directory.error=/Users/SOME_USER_NAME/BanzaiFolder/Error
    или
    java -jar fileloader-1.0.RELEASE.jar

В случае без указания папок, приложение по умолчанию выберет системную папку пользователя и создаст в ней папки BanzaiFolder/New, 
BanzaiFolder/Old, BanzaiFolder/Error.
Все необходимые параметры и комментарии к ним находятся в файле fileloader/src/main/resources/application.yml.


