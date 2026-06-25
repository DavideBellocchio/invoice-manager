# Invoice Manager

## Descrizione del progetto

**Invoice Manager** è un sistema di fatturazione pensato per le PMI, sviluppato come **backend REST API puro** senza interfaccia frontend.

## Stack Tecnologico

- Docker
- PostgreSQL
- Spring Boot 4.1.0
- Git

## Setup

### 1. Configurazione delle variabili d'ambiente

Creare il file `.env` partendo da `.env.example` e sostituire i valori di esempio con quelli desiderati:

```bash
cp .env.example .env
```

### 2. Configurazione dell'applicazione

Creare il file `application-dev.yml` partendo da `application-dev.yml.example` e aggiornare i parametri necessari:

```bash
cp src/main/resources/application-dev.yml.example src/main/resources/application-dev.yml
```

### 3. Avvio del database PostgreSQL

Avviare i container Docker utilizzando Docker Compose:

```bash
docker compose up -d
```

Questo comando creerà e avvierà il container PostgreSQL necessario al funzionamento dell'applicazione.

### 4. Avvio dell'applicazione

Dalla root del progetto, eseguire:

```bash
./mvnw spring-boot:run
```

L'applicazione sarà disponibile una volta completato il processo di avvio di Spring Boot.

## Requisiti

Assicurarsi di avere installato:

- Docker
- Docker Compose
- Java 21 (versione compatibile con Spring Boot 4.1.0)
- Maven Wrapper (`mvnw`) incluso nel progetto
- Git