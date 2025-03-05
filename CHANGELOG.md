## Breaking Changes
## Java 11
This library now requires Java 11 or higher.

## ServerStatus
The `ServerStatus` class is now an enum instead of a class with static `int` fields. Each status has a numeric
value (`getValue`), a display name (`getName`) and a brand color (`getColor`).

If a status code is unknown because the API client has not been updated `OFFLINE` will be returned.

### SLF4J Implementation
This library no longer depends directly on any SLF4J implementation. If you want to see log messages
from this library, you must include an SLF4J implementation in your project.

### Other
- `Server#subscribe` and `Server#unsubscribe` now accept the `StreamName` enum instead of any string
- Arrays have been Replaced by Collection's in almost all places
- Config options now return a generic type instead of `Object`
- Removed `ExarotonClient#getGson()` and `WebsocketClient#getGson()`
- Renamed `Server#setClient` to `Server#init` and added parameter `gson`

## Improvements
- Update dependencies
- Make jetbrains annotations compile only
