```bash
# ngrok http 8080
# http://127.0.0.1:4040/inspect/http

# http://localhost:8085/actuator/prometheus

# Run Grafana
docker run -d -p 3000:3000 --name grafana grafana/grafana-oss
localhost:3000
# user: admin
# pass: admin
```