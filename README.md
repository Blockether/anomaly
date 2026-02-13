# Anomaly

Blockether anomalies - standardized error categories based on [Cognitect anomalies](https://github.com/cognitect-labs/anomalies).

## Installation

Add the following dependency to your `deps.edn`:

```clojure
{:deps {com.blockether/anomaly {:mvn/version "1.0.0"}}}
```

## Usage

```clojure
(require '[com.blockether.anomaly.core :as anomaly])

;; Throw standardized errors
(anomaly/not-found! "User not found" {:user-id 123})
(anomaly/forbidden! "Access denied")
(anomaly/incorrect! "Invalid email format" {:field :email})

;; Create anomaly maps without throwing
(anomaly/anomaly ::anomaly/not-found "Resource missing" {:id 456})

;; Check HTTP status
(anomaly/http-status {::anomaly/category ::anomaly/forbidden}) ;; => 403

;; Predicates
(anomaly/anomaly? some-map)
(anomaly/client-error? anomaly-map)
(anomaly/server-error? anomaly-map)
```

## Anomaly Categories

| Category      | HTTP | Description                                    |
|---------------|------|------------------------------------------------|
| ::unavailable | 503  | Service temporarily unavailable, retry later   |
| ::interrupted | 500  | Operation was interrupted                      |
| ::incorrect   | 400  | Bad request, invalid input                     |
| ::forbidden   | 403  | Not authorized to perform this action          |
| ::unauthorized| 401  | Authentication required                        |
| ::not-found   | 404  | Resource not found                             |
| ::conflict    | 409  | Conflict with current state (e.g., duplicate)  |
| ::fault       | 500  | Internal server error                          |
| ::busy        | 503  | Server is busy, retry later                    |
| ::unsupported | 501  | Operation not supported                        |