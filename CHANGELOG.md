## Breaking Changes
## Java 11
This library now requires Java 11 or higher.

### SLF4J Implementation
This library no longer depends directly on any SLF4J implementation. If you want to see log messages
from this library, you must include an SLF4J implementation in your project.

### Other
- Arrays have been Replaced by Collection's in almost all places
- Removed `ExarotonClient#getGson()` and `WebsocketClient#getGson()`
- Renamed `Server#setClient` to `Server#init` and added parameter `gson`

## Improvements
- Update dependencies
- Make jetbrains annotations compile only
