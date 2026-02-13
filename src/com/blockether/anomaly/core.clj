(ns com.blockether.anomaly.core
  "Cognitect Anomalies - standardized error categories.
   
   Based on https://github.com/cognitect-labs/anomalies
   
   Anomalies are maps with ::category key indicating the error type.
   These map to HTTP status codes:
   
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
   
   Usage:
   (throw! ::forbidden \"You are not authorized to access this resource\")
   (throw! ::not-found \"Therapy plan not found\" {:plan-id plan-id})
   (throw! ::incorrect \"Invalid email format\" {:field :email})")

;; =============================================================================
;; Anomaly Categories
;; =============================================================================

(def categories
  "Set of all valid anomaly categories."
  #{::unavailable
    ::interrupted
    ::incorrect
    ::forbidden
    ::unauthorized
    ::not-found
    ::conflict
    ::fault
    ::busy
    ::unsupported})

;; =============================================================================
;; HTTP Status Mapping
;; =============================================================================

(def category->http-status
  "Maps anomaly category to HTTP status code."
  {::unavailable  503
   ::interrupted  500
   ::incorrect    400
   ::forbidden    403
   ::unauthorized 401
   ::not-found    404
   ::conflict     409
   ::fault        500
   ::busy         503
   ::unsupported  501})

(defn http-status
  "Returns HTTP status code for an anomaly.
   
   Params:
   `anomaly` - Map. Anomaly with ::category key.
   
   Returns:
   Integer. HTTP status code (defaults to 500 for unknown categories)."
  [anomaly]
  (get category->http-status (::category anomaly) 500))

;; =============================================================================
;; Anomaly Predicates
;; =============================================================================

(defn anomaly?
  "Returns true if x is an anomaly map.
   
   Params:
   `x` - Any. Value to check.
   
   Returns:
   Boolean."
  [x]
  (and (map? x)
       (contains? categories (::category x))))

(defn client-error?
  "Returns true if anomaly represents a client error (4xx).
   
   Params:
   `anomaly` - Map. Anomaly to check.
   
   Returns:
   Boolean."
  [anomaly]
  (let [status (long (http-status anomaly))]
    (and (>= status 400) (< status 500))))

(defn server-error?
  "Returns true if anomaly represents a server error (5xx).
   
   Params:
   `anomaly` - Map. Anomaly to check.
   
   Returns:
   Boolean."
  [anomaly]
  (>= (long (http-status anomaly)) 500))

;; =============================================================================
;; Exception Helpers
;; =============================================================================

(defn anomaly
  "Creates an anomaly map.
   
   Params:
   `category` - Keyword. One of the anomaly categories.
   `message` - String. Human-readable error message.
   `data` - Map, optional. Additional context data.
   
   Returns:
   Map. Anomaly with ::category, ::message, and any additional data.
   
   Examples:
   (anomaly ::not-found \"User not found\")
   => {::category ::not-found, ::message \"User not found\"}
   
   (anomaly ::forbidden \"Access denied\" {:user-id 123})
   => {::category ::forbidden, ::message \"Access denied\", :user-id 123}"
  ([category message]
   (anomaly category message nil))
  ([category message data]
   (merge {::category category
           ::message message}
          data)))

(defn throw!
  "Throws an ex-info exception with anomaly data.
   
   Params:
   `category` - Keyword. One of the anomaly categories.
   `message` - String. Human-readable error message.
   `data` - Map, optional. Additional context data.
   
   Throws:
   ExceptionInfo with anomaly map as ex-data.
   
   Examples:
   (throw! ::forbidden \"You cannot access this resource\")
   (throw! ::not-found \"Patient not found\" {:patient-id pid})"
  ([category message]
   (throw! category message nil))
  ([category message data]
   (throw (ex-info message (anomaly category message data)))))

;; =============================================================================
;; Common Error Throwers (Convenience Functions)
;; =============================================================================

(defn unauthorized!
  "Throws unauthorized (401) anomaly.
   
   Params:
   `message` - String, optional. Error message. Defaults to \"Authentication required\".
   `data` - Map, optional. Additional context."
  ([]
   (unauthorized! "Authentication required"))
  ([message]
   (unauthorized! message nil))
  ([message data]
   (throw! ::unauthorized message data)))

(defn forbidden!
  "Throws forbidden (403) anomaly.
   
   Params:
   `message` - String. Error message describing why access is denied.
   `data` - Map, optional. Additional context."
  ([message]
   (forbidden! message nil))
  ([message data]
   (throw! ::forbidden message data)))

(defn not-found!
  "Throws not-found (404) anomaly.
   
   Params:
   `message` - String. Error message describing what wasn't found.
   `data` - Map, optional. Additional context."
  ([message]
   (not-found! message nil))
  ([message data]
   (throw! ::not-found message data)))

(defn incorrect!
  "Throws incorrect (400) anomaly for validation errors.
   
   Params:
   `message` - String. Error message describing what's invalid.
   `data` - Map, optional. Additional context (e.g., field names, validation errors)."
  ([message]
   (incorrect! message nil))
  ([message data]
   (throw! ::incorrect message data)))

(defn conflict!
  "Throws conflict (409) anomaly.
   
   Params:
   `message` - String. Error message describing the conflict.
   `data` - Map, optional. Additional context."
  ([message]
   (conflict! message nil))
  ([message data]
   (throw! ::conflict message data)))

(defn fault!
  "Throws fault (500) anomaly for internal errors.
   
   Params:
   `message` - String. Error message (keep it generic for security).
   `data` - Map, optional. Additional context (for logging, not exposed to client)."
  ([message]
   (fault! message nil))
  ([message data]
   (throw! ::fault message data)))

(defn unavailable!
  "Throws unavailable (503) anomaly.
   
   Params:
   `message` - String. Error message describing why service is unavailable.
   `data` - Map, optional. Additional context."
  ([message]
   (unavailable! message nil))
  ([message data]
   (throw! ::unavailable message data)))

(defn unsupported!
  "Throws unsupported (501) anomaly.
   
   Params:
   `message` - String. Error message describing what's not supported.
   `data` - Map, optional. Additional context."
  ([message]
   (unsupported! message nil))
  ([message data]
   (throw! ::unsupported message data)))

;; =============================================================================
;; Exception Extraction
;; =============================================================================

(defn ex-anomaly
  "Extracts anomaly from an exception, if present.
   
   Params:
   `e` - Exception. The exception to extract from.
   
   Returns:
   Map or nil. The anomaly map if exception contains one, nil otherwise."
  [e]
  (when (instance? clojure.lang.ExceptionInfo e)
    (let [data (ex-data e)]
      (when (anomaly? data)
        data))))

#_{:clojure-lsp/ignore [:clojure-lsp/unused-public-var]}
(defn ex->anomaly
  "Converts any exception to an anomaly.
   
   If the exception already contains anomaly data, returns that.
   Otherwise, wraps it as a ::fault anomaly.
   
   Params:
   `e` - Exception. The exception to convert.
   
   Returns:
   Map. An anomaly map."
  [e]
  (or
   (ex-anomaly e)
   (anomaly ::fault (ex-message e))))
