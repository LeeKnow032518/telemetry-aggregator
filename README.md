## Telemetry Aggregator

Сервис Telemetry Aggregator занимается агрегацией данных по роутерам, устройствам и сервисам, которые считывает из Cassandra по тайм-окну (например, за последние 2 минуты), после чего записывает агрегированные данные в PostgreSQL в соответствующие таблицы.

Сервис рассчитывает метрики: общее кол-во трафика, средняя задержка, средний score и т.д.


## Возможности

- Считывание данных с Cassandra по временному окну
- Масштабируемое количество записей для агрегации
- Агрегация данных по роутерам, устройствам и сервисам
- Запись агрегированных данных в таблицы в Postgres
- Конфигурируемость через `.env` или `application.yml`

# Таблицы:

- aggregated_router_stats
- aggregated_device_stats
- aggregated_service_stats

# Архитектура (C4-модели)
- [Context](Context.png)
- [Container](Container.png)
- [Component](Component.png)
- [Sequence](Sequence.png)

### Запуск окружения [prod|local]

Необходимо выбрать окружение, в котором будет запускаться сервис.

Убедитесь, что файл `.env.[prod|local]` существует в корне проекта и содержит все необходимые переменные окружения. Затем выполните:

```bash
docker-compose -f docker-compose.[prod|local].yml --env-file .env.prod.[prod|local] up --build
```

Это соберёт образ и запустит все необходимые сервисы, если выбрано окружение [local]: Cassandra, Postgres и telemetry-aggregator-local.

### ⏹ Остановка и очистка

Для корректной остановки и удаления томов используйте:

```bash
docker-compose -f docker-compose.[prod|local].yml --env-file .env.prod.[prod|local] down -v
```

## Отслеживание метрик 
\
Для отслеживания метрик используются дашборды в Grafana. При переходе по адресу http://localhost:3000 во вкладке Dashboards доступны следующие дашборды:
- **Cassandra dashboard** - для отслеживания задержек в операциях чтения и записи в Cassandra
- **PostgreSQL Database** - отслеживание метрик для PostgreSQL
- **Telemetry aggregator dashboard** - отслеживание длительности одной агрегации, количества прочитанных из Cassandra данных для агрегации и записанных в таблицы в Postgres агрегированных данных и логов сервиса
