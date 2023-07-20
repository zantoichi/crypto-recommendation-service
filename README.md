# Crypto Recommendation Service

## Usage

- Retrieve Sorted List of Cryptos: To get a descending sorted list of all cryptocurrencies based on their normalized
  range, send a GET request to `/cryptos/sorted`.

- Retrieve Crypto Statistics: To get the oldest, newest, minimum, and maximum values for a specific cryptocurrency, send
  a GET request to `/cryptos/{cryptoSymbol}/statistics`, replacing `{cryptoSymbol}` with the symbol of the
  cryptocurrency.

- Retrieve Crypto with Highest Normalized Range: To get the cryptocurrency with the highest normalized range for a
  specific day, send a GET request to `/cryptos/highest-range/{day}`, replacing `{day}` with the specific day in the
  format `yyyy-MM-dd`.

## Docker Image Generation

The project supports generating a Docker image using the `bootBuildImage` Gradle task, docker daemon is required.
Execute the following command:

```shell
./gradlew bootBuildImage
```

The generated Docker image contains the application and its dependencies.

## Rate Limiting

The service implements rate limiting to protect against abuse from malicious users.
Rate limiting is based on the IP address of the client making the requests.
By default, a maximum of 5 requests every 10 seconds per IP address is allowed,
but this is configurable.

## Configuration

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
    limit: 5
    timeout-duration: 0
```

Refer to the Swagger UI documentation available at http://localhost:8081/swagger-ui.html for documentation on using the
API.
