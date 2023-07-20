# Crypto Recommendation Service

A service that provides valuable insights about the market of cryptocurrencies.

## 🚀 Usage

The service provides several APIs to fetch data:

| Endpoint                                 | Description                                                                                                                                                      |
|------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `GET /cryptos/sorted`                    | Retrieve a descending sorted list of all cryptocurrencies based on their normalized range.                                                                       |
| `GET /cryptos/{cryptoSymbol}/statistics` | Retrieve statistics (oldest, newest, minimum, and maximum values) for a specific cryptocurrency. Replace `{cryptoSymbol}` with the symbol of the cryptocurrency. |
| `GET /cryptos/highest-range/{day}`       | Retrieve the cryptocurrency with the highest normalized range for a specific day. Replace `{day}` with the specific day in the format `yyyy-MM-dd`.              |

## 🐳 Docker Image Generation

The project supports generating a Docker image using the `bootBuildImage` Gradle task. Docker daemon is required. Run
the following command to build the image:

```shell
./gradlew bootBuildImage
```

The generated Docker image contains the application and its dependencies.

## 🛡️ Rate Limiting

The service implements rate limiting to protect against abuse from malicious users.
Rate limiting is based on the IP address of the client making the requests.
By default, a maximum of 20 requests every 10 seconds per IP address is allowed,
but this is configurable.

## ⚙️ Configuration

The application uses `application.yml` for configuration.
Below is an example configuration snippet:

```yaml
csv:
    reader:
        directory-path: src/main/resources/prices/
        file-suffix: _values.csv
        timestamp-header: timestamp
        symbol-header: symbol
        price-header: price

app:
    allowed-crypto:
        - BTC
        - ETH
        - DOGE
        - LTC
        - XRP

rate-limiter:
    refresh-period: 10s
    limit: 20
    timeout-duration: 0
```

## 📚 API Documentation

Refer to the Swagger UI documentation available at http://localhost:8081/swagger-ui.html for documentation on using the
API.
