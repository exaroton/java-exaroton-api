## Breaking Changes
## Java 11
This library now requires Java 11 or higher.

## Async API
All methods that make requests to the Exaroton API are now asynchronous. This means that all methods that return a value
now return a `CompletableFuture` instead. To get the result of the request you can use `CompletableFuture#get()` or
`CompletableFuture#join()`.

This also changes where exceptions are thrown. Now the following rules apply:
- IOExceptions are thrown directly by the API methods
- APIExceptions cause the CompletableFuture to complete exceptionally

If you use `join()` or `get()` a `CompletionException` containing the `APIException` will be thrown.

## Fetch methods
Many objects can be obtained from the API client without fetching their data (e.g. Server, CreditPool, ServerFile, ...).
The method to fetch their data has been renamed from `get` to `fetch`. It now also offers an override with a boolean 
parameter that can be used to only fetch the object once `server.fetch(false)`.

## ServerStatus
The `ServerStatus` class is now an enum instead of a class with static `int` fields. Each status has a numeric
value (`getValue`), a display name (`getName`) and a brand color (`getColor`).

If a status code is unknown because the API client has not been updated `OFFLINE` will be returned.

### SLF4J Implementation
This library no longer depends directly on any SLF4J implementation. If you want to see log messages
from this library, you must include an SLF4J implementation in your project.

### API Requests

#### Request Bodies
`ApiRequest#getBody()` and `ApiRequest#getInputStream()` have been replaced by `ApiRequest#getBodyPublisher()`. For a 
JSON body you can use `ApiRequest#jsonBodyPublisher(Object)`. This only affects users who extended the request classes.

`PutFileDataRequest`'s constructor has been changed to accept a `Supplier<InputStream>` instead of an `InputStream`.

#### Request Methods
`ApiRequest#requestRaw()`, `ApiRequest#requestString()` and `ApiRequest#request()` have been replaced by 
`ExarotonClient#request(ApiRequest, HttpResponse.BodyHandler)` use the respective body handlers to get an input stream,
string or object.

### WebSockets
The `java-websocket` library has been replaced by the built-in Java 11 websocket implementation. Unless you were using
the `WebSocketClient` or `WebSocketManager` classes directly this shouldn't require any changes to your code.

`Server#subscribe` and `Server#unsubscribe` now accept the `StreamName` enum instead of any string

The debug/error handler methods have been removed. Errors and debug messages are now  exclusively logged using SLF4J

#### Subscribers
The `Subscriber` classes are now interfaces and their methods have been renamed to make the more explicit and reduce
the chance of conflicts with other overrides.

### Other
- Arrays have been Replaced by Collection's in almost all places
- Nullable return types have been replaced by Optional's
- Public final properties have been replaced by getters
- Config options now return a generic type instead of `Object`
- Renamed `ServerFile#getInfo` to `ServerFile#get`
- Removed `ExarotonClient#getGson()` and `WebsocketClient#getGson()`
- Removed `Server#setClient` and `CreditPool#setClient`
- Removed `ExarotonClient#getBaseUrl()` and `ExarotonClient#createConnection(String, String)`
- Many classes are now final
- Added `ApiStatus` annotations to many classes and methods
- Removed `ExarotonClient#getApiToken()`
- `ExarotonClient#setProtocol` and `ExarotonClient#getProtocol` have been removed as HTTP is not supported by the API

## Improvements
- Update dependencies
- Make jetbrains annotations compile only
