# PointSense - Render + PostgreSQL Deployment

Bu proje icin dogru secim PostgreSQL'dir.
H2 gecici/test icin iyidir ama production icin uygun degildir.

## Neden PostgreSQL?
- Kalici veri saklama
- Render managed PostgreSQL destegi
- Spring Data JPA ile uyumlu
- Scale ve backup kabiliyeti

## Render'da Onerilen Mimari
1. Render PostgreSQL (managed) olustur.
2. Render Web Service (Dockerfile ile) olustur.
3. Web Service environment variable'larini asagidaki gibi gir.

## Web Service Env Vars
- PORT=8080
- PGHOST=<render_postgres_host>
- PGPORT=<render_postgres_port>
- PGDATABASE=<render_postgres_database>
- PGUSER=<render_postgres_user>
- PGPASSWORD=<render_postgres_password>
- MODEL_PATH=model.onnx (opsiyonel)

Not: Bu proje su an `application.properties` icinde PGHOST/PGPORT/PGDATABASE/PGUSER/PGPASSWORD bekliyor.

## Local gelistirme (Docker + Postgres)
`docker-compose.yml` eklendi.

Calistirma:
```bash
docker compose up --build
```

API:
- http://localhost:8080/maps
- http://localhost:8080/swagger-ui.html

## Production kontrol listesi
- Render DB baglantisi test edildi
- `/actuator/health` 200 donuyor
- Swagger aciliyor (`/swagger-ui.html`)
- Harita kaydet/getir/sil endpointleri dogrulandi
