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


### Other
- `Server#subscribe` and `Server#unsubscribe` now accept the `StreamName` enum instead of any string
- Arrays have been Replaced by Collection's in almost all places
- Config options now return a generic type instead of `Object`
- Renamed `ServerFile#getInfo` to `ServerFile#get`
- Removed `ExarotonClient#getGson()` and `WebsocketClient#getGson()`
- Removed `Server#setClient` and `CreditPool#setClient`
- Removed `ExarotonClient#getBaseUrl()` and `ExarotonClient#createConnection(String, String)`
- Many classes are now final
- Added `ApiStatus` annotations to many classes and methods
- Removed debug/error handler from `WebsocketManager` and `WebsocketClient`. Errors and debug messages are now
  exclusively logged using SLF4J

## Improvements
- Update dependencies
- Make jetbrains annotations compile only
