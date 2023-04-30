# PushMoney

**Outline**

- [Run](#run)
- [Change run scripts](#change-run-scripts)
- [Configure Grafana](#configure-grafana)

### Run

**Note: Run all scripts from `push-service-new` directory.**

```shell
cd /path/to/push-service-new

# Build
./sh/build.sh

# Start containers
./sh/start.sh

# Stop containers
./sh/stop.sh
```

### Change run scripts

To build only selected containers change in `sh/build.sh`

```shell
# all
docker compose up --force-recreate -d

# selected services
#docker compose up --force-recreate -d postgres redis push-service
```

to

```shell
# all
#docker compose up --force-recreate -d

# selected services
docker compose up --force-recreate -d postgres redis push-service
```

Same with start/stop containers scripts.

### Configure Grafana

1. Go to http://localhost:3030
2. Login with username: admin, password: admin
3. Add data source > Prometheus > http://prometheus:9090
4. Import dashboard > by id > 4701

