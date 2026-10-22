import json
import os
import time
import urllib.error
import urllib.request

CONNECT_URL = os.getenv(
    "CONNECT_URL",
    "http://debezium:8083"
)

CONNECTOR_NAME = "ordersys-outbox-connector"
CONFIG_FILE = "/config/register-outbox-connector.json"

with open(CONFIG_FILE, "r", encoding="utf-8") as file:
    document = json.load(file)

config = document["config"]

url = f"{CONNECT_URL}/connectors/{CONNECTOR_NAME}/config"

body = json.dumps(config).encode("utf-8")

for attempt in range(60):
    request = urllib.request.Request(
        url,
        data=body,
        headers={"Content-Type": "application/json"},
        method="PUT",
    )

    try:
        with urllib.request.urlopen(request, timeout=5) as response:
            print(
                f"Connector registrado/atualizado: "
                f"{CONNECTOR_NAME} (HTTP {response.status})"
            )
            break

    except (urllib.error.URLError, TimeoutError, OSError) as error:
        if attempt == 59:
            raise RuntimeError(
                "Não foi possível registrar o connector "
                "após várias tentativas."
            ) from error

        print(
            f"Debezium ainda não está pronto "
            f"(tentativa {attempt + 1}/60). Aguardando..."
        )
        time.sleep(5)
