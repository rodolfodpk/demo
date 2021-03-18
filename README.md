
## Steps

1. Clone crabzilla:

```bash
git clone https://github.com/crabzilla/crabzilla
cd crabzilla
```

2. Open another terminal and build it, skipping running tests:

```bash
mvn clean install -DskipTests=true
```

3. Now clone this demo and build it

```bash
git clone https://github.com/rodolfodpk/demo
cd demo
gradle build
```

4. Now let's start the NATS

```bash
sudo docker run -d --name=nats-stream -p 4222:4222 -p 6222:6222 -p 8222:8222  nats-streaming --user al --pass alisson
```

4. Now let's start the Postgres database

```bash
cd demo
docker-compose up
```

5. Finally, let's run this demo application

```bash
cd demo
gradle run
```

6. Then lets do a request:

```bash
wget -O- http://localhost:8080/hello
```

## Notes

1. NATS is running in memory, so the messages are not persistent.
2. Since we are using an AtomicInteger to generate ids, every time you starts the application you need to recreate the database:

```bash
cd demo
docker-compose down -v
docker-compose up
```

## Design

![GitHub Logo](/cqrs-arch.png)