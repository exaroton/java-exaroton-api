# Java exaroton API Client

---
## About
The official java library for the [exaroton API](https://developers.exaroton.com/) 
that can be used to automatically manage Minecraft servers (e.g. starting or stopping them).

Required Java Version: 11+

If you're creating a plugin/mod that runs on an exaroton server, you can get the current server using client.getCurrentServer().

## Installing
Gradle:
```gradle
dependencies {
    implementation 'com.exaroton:api:2.2.1'
}
```

Maven:
```xml
<dependency>
  <groupId>com.exaroton</groupId>
  <artifactId>api</artifactId>
  <version>2.2.1</version>
</dependency>
```

## Usage 
You need an API key to use the API. You can generate an api key in the [account options](https://exaroton.com/account/).


#### Create a client

```jshelllanguage
ExarotonClient client = new ExarotonClient("example-api-token");
```

### REST API
All rest methods are async and return a CompletableFuture. Use `CompletableFuture#join()`, `CompletableFuture#get()`
or `CompletableFuture#thenAccept()` to get the result.

#### Show account info
```jshelllanguage
ExarotonClient client = new ExarotonClient("example-api-token");

Account account = client.getAccount().join();
System.out.println("My account " + account.getName() + " has " + account.getCredits() + "!");
```
Objects of the Account class contain getters for each field in the [API docs](https://developers.exaroton.com/#account-get). 

#### Get all servers
```jshelllanguage
ExarotonClient client = new ExarotonClient("example-api-token");

List<Server> servers = client.getServers().join();
for (Server server: servers) {
    System.out.println(server.getId() + ":" + server.getAddress());
}
```
Objects of the Server class contain getters for each field in the [API docs](https://developers.exaroton.com/#servers-get-1).


#### Get a single server
```jshelllanguage
ExarotonClient client = new ExarotonClient("example-api-token");

// This method does not fetch the server, it just creates an empty object with the id
Server server = client.getServer("tgkm731xO7GiHt76");

// This method actually fetches the server from the API
server.fetch().join();

System.out.println(server.getAddress());
server.start().join();
server.restart().join();
server.stop().join();
server.executeCommand("say hello world").join();
```

#### Check the server status
```jshelllanguage
ExarotonClient client = new ExarotonClient("example-api-token");

Server server = client.getServer("tgkm731xO7GiHt76");
server.fetch().join();
System.out.println(server.getStatus());
// getStatus returns an instance of the ServerStatus enum with a numeric value, display name and color.

if (server.hasStatus(ServerStatus.ONLINE)) {
    System.out.println("Server is online!");
}
else if (server.hasStatus(ServerStatus.OFFLINE)) {
    System.out.println("Server is offline!");
}
else if (server.hasStatus(ServerStatus.PREPARING, ServerStatus.LOADING, ServerStatus.STARTING)) {
    System.out.println("Server is starting!");
}
```


#### Get/Share your server log
```jshelllanguage
ExarotonClient client = new ExarotonClient("example-api-token");

Server server = client.getServer("tgkm731xO7GiHt76");
// Get your log
ServerLog log = server.getLog().join();
System.out.println(log.getContent());

// Send your log to the mclogs API
MclogsData mclogs = server.shareLog().join();
System.out.println(mclogs.getUrl());
```
The result is cached and will not return the latest updates immediately. It's not possible to get the server logs while the server is loading, stopping or saving.


#### Get/Set server RAM
```jshelllanguage
ExarotonClient client = new ExarotonClient("example-api-token");

Server server = client.getServer("tgkm731xO7GiHt76");
ServerRAMInfo ram = server.getRAM().join();
System.out.println("Current RAM: " + ram.getRam() + "GiB");
ram = server.setRAM(8).join();
System.out.println("New RAM: " + ram.getRam() + "GiB");
```
RAM values are in full GiB and have to be between 2 and 16. Proxy servers can have as little as 1 GiB of RAM.

#### Player lists
A player list is a list of players such as the whitelist, ops or bans. 
Player list entries are usually usernames, but might be something else, e.g. IPs in the banned-ips list. 
All player list operations are storage operations that might take a while, so try to reduce the amount of requests and combine actions when possible (e.g. adding/deleting multiple entries at once). 
Player lists are also cached any might not immediately return new results when changed through other methods e.g. in-game.

You can list all available playerlists using server.getPlayerLists()

##### List/modify entries
```jshelllanguage
ExarotonClient client = new ExarotonClient("example-api-token");

Server server = client.getServer("tgkm731xO7GiHt76");
PlayerList whitelist = server.getPlayerList("whitelist");
System.out.println("Whitelist:");
for (String entry: whitelist.getEntries().join()) {
    System.out.println(entry);
}
whitelist.add("example", "example2").join();
whitelist.remove("example34").join();
```

#### Files
To manage a file on your server first obtain a file Object:
```jshelllanguage
ExarotonClient client = new ExarotonClient("example-api-token");

Server server = client.getServer("tgkm731xO7GiHt76");
ServerFile file = server.getFile("/whitelist.json");
```

Now you can fetch file info, get the context of a file (if it's a text file) or download it.
```jshelllanguage
whitelist.fetch().join();
if (file.isTextFile()) {
    System.out.println(file.getContent());
}
else {
    file.download(Paths.get("whitelist.json")).join();    
}
```

You can also write to the file or upload a file:
```jshelllanguage
file.putContent("I can write to a file o.O").join();
file.upload(Paths.get("other-whitelist.json")).join();
```

Deleting files and creating directories is possible as well:
```jshelllanguage
file.delete().join();
file.createAsDirectory().join();
```

#### Configs
Some files are special because they are parsed, validated and understood by the exaroton backend.
These files are called configs and can be managed like this:
```jshelllanguage
ExarotonClient client = new ExarotonClient("example-api-token");

Server server = client.getServer("tgkm731xO7GiHt76");
ServerFile file = server.getFile("/server.properties");
ServerConfig config = file.getConfig();

Map<String, ConfigOption<?>> options = config.getOptions().join();
for (ConfigOption<?> option: options.values()) {
    System.out.println(option.getKey() + ": " + option.getValue());
}

ConfigOption<?> option = config.getOption("level-seed").join();
```

There are several types of options which extend the ServerConfigOption class:
```jshelllanguage
for (ConfigOption<?> option: options.values()) {
    if (option.getType() == OptionType.BOOLEAN) {
        BooleanConfigOption booleanOption = (BooleanConfigOption) option;
        System.out.println(booleanOption.getKey() + ": " + booleanOption.getValue());
    }
}
```

To save changes to a config, use the save() method:
```jshelllanguage
if (options.get("level-seed") instanceof StringConfigOption stringOption) {
    stringOption.setValue("example");
}
config.save().join();
```

#### Credit Pools
Credit pools allow you to share payments for your server with other users in a safe way.
You can view information about credit pools like this:
```jshelllanguage
// get all credit pools
List<CreditPool> pools = client.getCreditPools().join();
for (CreditPool pool: pools) {
    System.out.println(pool.getName() + ": " + pool.getCredits());
}

// get a single credit pool
CreditPool pool = client.getCreditPool("N2t9gWOMpzRL37FI");
pool.fetch(); // update pool info
System.out.println(pool.getName() + ": " + pool.getCredits());
```

The API also allows you to fetch the servers in a pool:
```jshelllanguage
CreditPool pool = client.getCreditPool("N2t9gWOMpzRL37FI");
List<Server> servers = pool.getServerList().join();
for (Server server: servers) {
    System.out.println(server.getName() + ": " + server.getAddress());
}
```

If you have the "View members" permission, you can even get all members of a pool:
```jshelllanguage
CreditPool pool = client.getCreditPool("N2t9gWOMpzRL37FI");
List<CreditPoolMember> members = pool.getMemberList().join();
for (CreditPoolMember member: members) {
    System.out.println(member.getName() + ": " + member.getCredits());
}
```


## Websocket API
The websocket API allows a constant connection to our websocket service to receive events in real time without polling.
When you attach a subscriber to a server, a websocket connection will be established automatically. This library also
subscribes to the correct streams once you register a subscriber needing other streams.

#### Server status changes 
You can react to server status changes by adding a subscriber: 

```jshelllanguage
ExarotonClient client = new ExarotonClient("example-api-token");

Server server = client.getServer("tgkm731xO7GiHt76");
server.addStatusSubscriber(new ServerStatusSubscriber() {
    @Override
    public void handleStatusUpdate(Server oldServer, Server newServer) {
        System.out.printf("Server had status %s and now has status %s!%n", oldServer.getStatus(), newServer.getStatus());
    }
});
```
This event is not only triggered when the status itself changes but also when other events happen, 
e.g. a player joins the server.

It's also possible to wait until your server reaches a specific status using the websocket API:
```jshelllanguage
server.waitForStatus(ServerStatus.OFFLINE, ServerStatus.CRASHED).join();
```

It is highly recommended to consider all possible status changes and/or configure a timeout to prevent your application
from hanging e.g. if starting the server fails or the server crashes while stopping. To set a timeout just use
`Future#get(long,TimeUnit)`:

```jshelllanguage
try {
    server.waitForStatus(ServerStatus.OFFLINE, ServerStatus.CRASHED).get(5, TimeUnit.MINUTES);
} catch (TimeoutException e) {
    System.out.println("Server did not reach the desired status in time!");
}
```

#### Console messages
The console stream emits an event for every new console line.
```jshelllanguage
ExarotonClient client = new ExarotonClient("example-api-token");

Server server = client.getServer("tgkm731xO7GiHt76");
server.addConsoleSubscriber(new ConsoleSubscriber() {
    @Override
    public void handleLine(String line) {
        System.out.println(line);
    }
});
```

The console stream also allows you to send commands directly over the websocket. This is faster because
the connection is already established and no further authorization etc. is necessary. This library
already checks if you are subscribed to the console stream and sends the command through that stream
instead, so you can just use it the same way as [before](#get-a-single-server).

#### Tick times
On Minecraft Java edition servers with version 1.16 and higher it is possible to get the tick times, 
and the TPS (ticks per second) of your server. This information is also available as an optional stream.

```jshelllanguage
ExarotonClient client = new ExarotonClient("example-api-token");

Server server = client.getServer("tgkm731xO7GiHt76");
server.addTickSubscriber(new TickSubscriber() {
    @Override
    public void handleTickData(TickData tick) {
        System.out.printf("Average tick time: %s%nCalculated TPS: %s%n",
                tick.getAverageTickTime(), tick.calculateTPS());
    }
});
```
The tps are calculated by dividing 1000 by the average tick time and limiting it to 20.

#### RAM usage
There are two different optional streams to get RAM usage, the general stats stream and the Java specific 
heap stream. It is recommended to use the heap stream if you are running a server software that is based 
on Java. It is not recommended to use both.

```jshelllanguage
ExarotonClient client = new ExarotonClient("example-api-token");

Server server = client.getServer("tgkm731xO7GiHt76");
server.addStatsSubscriber(new StatsSubscriber() {
    @Override
    public void handleStats(StatsData stats) {
        System.out.printf("%s (%s)%n", stats.getMemory().getUsage(), stats.getMemory().getPercent());
    }
});
server.addHeapSubscriber(new HeapSubscriber() {
    @Override
    public void handleHeapUsage(HeapUsage heap) {
        System.out.println(heap.getUsage());
    }
});
```

#### Unsubscribe
When all subscribers for a stream are removed, this library automatically unsubscribes from events for that stream. Once
all streams are closed the websocket connection is closed as well.
You can manually close the websocket connection using the `Server#unsubscribe()`, e.g. as a cleanup step to make sure
the process stops even if there are dangling event handlers.

```jshelllanguage
server.unsubscribe(); // closes websocket connection
```

### Logging
This library uses `slf4j` for logging, but does not include any provider on its own. See the SLF4J docs for more
information on which providers are available or how to install them: https://www.slf4j.org/manual.html#swapping
