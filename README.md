## Java exaroton API Client

---
### About
The official java library for the [exaroton API](https://developers.exaroton.com/) 
that can be used to automatically manage Minecraft servers (e.g. starting or stopping them).

Required Java Version: 8+

If you're creating a plugin/mod that runs on an exaroton server, you can get the current server using client.getCurrentServer().

### Installing
Gradle:
```gradle
dependencies {
    implementation 'com.exaroton:api:1.6.2' //write implementation include('com.exaroton:api:1.6.2') to include the api within your jar
}
```

Maven:
```xml
<dependency>
  <groupId>com.exaroton</groupId>
  <artifactId>api</artifactId>
  <version>1.6.2</version>
</dependency>
```

### Usage 
You need an API key to use the API. You can generate an api key in the [account options](https://exaroton.com/account/).


#### Create a client

```java
ExarotonClient client = new ExarotonClient("example-api-token");
```

### REST API

#### Show account info
```java
ExarotonClient client = new ExarotonClient("example-api-token");

try {
    Account account = client.getAccount();
    System.out.println("My account " + account.getName() + " has " + account.getCredits() + "!");
} catch (APIException e) {
    e.printStackTrace();
}
```
Objects of the Account class contain getters for each field in the [API docs](https://developers.exaroton.com/#account-get). 

#### Get all servers
```java
ExarotonClient client = new ExarotonClient("example-api-token");

try {
    Server[] servers = client.getServers();
    for (Server server: servers) {
        System.out.println(server.getId() + ":" + server.getAddress());
    }
} catch (APIException e){
    e.printStackTrace();
}
```
Objects of the Server class contain getters for each field in the [API docs](https://developers.exaroton.com/#servers-get-1).


#### Get a single server
```java
ExarotonClient client = new ExarotonClient("example-api-token");

Server server = client.getServer("tgkm731xO7GiHt76");
try {
    server.get();
    System.out.println(server.getAddress());
    server.start();
    server.restart();
    server.stop();
    server.executeCommand("say hello world");
} catch (APIException e) {
    e.printStackTrace();
}
```

#### Check the server status
```java
ExarotonClient client = new ExarotonClient("example-api-token");

Server server = client.getServer("tgkm731xO7GiHt76");
try {
    server.get();
    System.out.println(server.getStatus());

    if (server.hasStatus(ServerStatus.ONLINE)) {
        System.out.println("Server is online!");
    }
    else if (server.hasStatus(ServerStatus.OFFLINE)) {
        System.out.println("Server is offline!");
    }
    else if (server.hasStatus(new int[]{ServerStatus.PREPARING, ServerStatus.LOADING, ServerStatus.STARTING})) {
        System.out.println("Server is starting!");
    }
} catch (APIException e) {
    e.printStackTrace();
}
```
The server status is an integer.
Status codes:
- 0 = OFFLINE
- 1 = ONLINE
- 2 = STARTING
- 3 = STOPPING
- 4 = RESTARTING
- 5 = SAVING
- 6 = LOADING
- 7 = CRASHED
- 8 = PENDING
- 10 = PREPARING

You can use the ServerStatus class to easily get the value of any status.

#### Get/Share your server log
```java
ExarotonClient client = new ExarotonClient("example-api-token");

Server server = client.getServer("tgkm731xO7GiHt76");
try {
    // Get your log
    ServerLog log = server.getLog();
    System.out.println(log.getContent());

    // Send your log to the mclogs API
    MclogsData mclogs = server.shareLog();
    System.out.println(mclogs.getUrl());
} catch (APIException e) {
    e.printStackTrace();
}
```
The result is cached and will not return the latest updates immediately. It's not possible to get the server logs while the server is loading, stopping or saving.


#### Get/Set server RAM
```java
ExarotonClient client = new ExarotonClient("example-api-token");

Server server = client.getServer("tgkm731xO7GiHt76");
try {
    ServerRAMInfo ram = server.getRAM();
    System.out.println("Current RAM: " + ram.getRam() + "GiB");
    ram = server.setRAM(8);
    System.out.println("New RAM: " + ram.getRam() + "GiB");
} catch (APIException e) {
    e.printStackTrace();
}
```
RAM values are in full GiB and have to be between 2 and 16.

#### Player lists
A player list is a list of players such as the whitelist, ops or bans. 
Player list entries are usually usernames, but might be something else, e.g. IPs in the banned-ips list. 
All player list operations are storage operations that might take a while, so try to reduce the amount of requests and combine actions when possible (e.g. adding/deleting multiple entries at once). 
Player lists are also cached any might not immediately return new results when changed through other methods e.g. in-game.

You can list all available playerlists using server.getPlayerLists()

##### List/modify entries
```java
ExarotonClient client = new ExarotonClient("example-api-token");

Server server = client.getServer("tgkm731xO7GiHt76");
PlayerList whitelist = server.getPlayerList("whitelist");
try {
    System.out.println("Whitelist:");
    for (String entry: whitelist.getEntries()) {
        System.out.println(entry);
    }
    whitelist.add("example", "example2");
    whitelist.remove("example34");
} catch (APIException e) {
    e.printStackTrace();
}
```

#### Files
To manage a file on your server first obtain a file Object:
```java
ExarotonClient client = new ExarotonClient("example-api-token");

Server server = client.getServer("tgkm731xO7GiHt76");
ServerFile file = server.getFile("/whitelist.json");
```

Now you can fetch file info, get the context of a file (if it's a text file) or download it.
```java
file.getInfo();
if (file.isTextFile()) {
    System.out.println(file.getContent());
}
else {
    file.download(Paths.get("whitelist.json"));    
}
```

You can also write to the file or upload a file:
```java
file.putContent("I can write to a file o.O");
file.upload(Paths.get("other-whitelist.json"));
```

Deleting files and creating directories is possible as well:
```java
file.delete();
file.createAsDirectory();
```

#### Configs
Some files are special because they are parsed, validated and understood by the exaroton backend.
These files are called configs and can be managed like this:
```java
ExarotonClient client = new ExarotonClient("example-api-token");

Server server = client.getServer("tgkm731xO7GiHt76");
ServerFile file = server.getFile("/server.properties");
ServerConfig config = file.getConfig();

Map<String, ServerConfigOption> options = config.getOptions();
for (ServerConfigOption option: options) {
    System.out.println(option.getName() + ": " + option.getValue());
}

ConfigOption option = config.getOption("level-seed");
```

There are several types of options which extend the ServerConfigOption class:
```java
for (ServerConfigOption option: options) {
    if (option.getType() == OptionType.BOOLEAN) {
        BooleanConfigOption booleanOption = (BooleanConfigOption) option;
        System.out.println(booleanOption.getName() + ": " + booleanOption.getValue());
    }
}
```

To save changes to a config, use the save() method:
```java
config.getOption("level-seed").setValue("example");
config.save();
```

#### Credit Pools
Credit pools allow you to share payments for your server with other users in a safe way.
You can view information about credit pools like this:
```java
// get all credit pools
CreditPool[] pools = client.getCreditPools();
for (CreditPool pool: pools) {
    System.out.println(pool.getName() + ": " + pool.getCredits());
}

// get a single credit pool
CreditPool pool = client.getCreditPool("N2t9gWOMpzRL37FI");
pool.get(); // update pool info
System.out.println(pool.getName() + ": " + pool.getCredits());
```

The API also allows you to fetch the servers in a pool:
```java
CreditPool pool = client.getCreditPool("N2t9gWOMpzRL37FI");
Server[] servers = pool.getServers();
for (Server server: servers) {
System.out.println(server.getName() + ": " + server.getAddress());
}
```

If you have the "View members" permission, you can even get all members of a pool:
```java
CreditPool pool = client.getCreditPool("N2t9gWOMpzRL37FI");
CreditPoolMember[] members = pool.getMembers();
for (CreditPoolMember member: members) {
    System.out.println(member.getName() + ": " + member.getCredits());
}
```


## Websocket API
The websocket API allows a constant connection to our websocket service to receive events in real time without polling 
(e.g. trying to get the server status every few seconds).

#### Server status changes
You can simply connect to the websocket API for a server by running the subscribe() function.
By default, you are always subscribed to server status update events, you can react to server status 
changes by adding a subscriber: 

```java
ExarotonClient client = new ExarotonClient("example-api-token");

Server server = client.getServer("tgkm731xO7GiHt76");
server.subscribe();
server.addStatusSubscriber(new ServerStatusSubscriber() {
    @Override
    public void statusUpdate(Server oldServer, Server newServer) {
        System.out.printf("Server had status %s and now has status %s!%n", oldServer.getStatus(), newServer.getStatus());
    }
});
```
This event is not only triggered when the status itself changes but also when other events happen, 
e.g. a player joins the server.

#### Console messages
One of the optional streams is the console stream. You can subscribe to one or more optional streams 
using the subscribe method. The console stream emits an event for every new console line.
```java
ExarotonClient client = new ExarotonClient("example-api-token");

Server server = client.getServer("tgkm731xO7GiHt76");
server.subscribe("console");
server.addConsoleSubscriber(new ConsoleSubscriber() {
    @Override
    public void line(String line) {
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

```java
ExarotonClient client = new ExarotonClient("example-api-token");

Server server = client.getServer("tgkm731xO7GiHt76");
server.subscribe("tick");
server.addTickSubscriber(new TickSubscriber() {
    @Override
    public void tick(TickData tick) {
        System.out.printf("Average tick time: %s%nCalculated TPS: %s%n", 
            tick.getAverageTickTime(), tick.calculateTPS());
    }
});
```
The tps are calculated by dividing 1000 by the average tick time and limiting it to 20.

#### RAM usage
There are two different optional streams to get RAM usage, the general stats stream and the Java specific 
heap stream. It is recommended to use the heap stream if you are running a server software that is based 
on Java. It is not recommended using both.

You can subscribe to multiple streams at once by passing an array to the subscribe function.

```java
ExarotonClient client = new ExarotonClient("example-api-token");

Server server = client.getServer("tgkm731xO7GiHt76");
server.subscribe("stats", "heap");
server.addStatsSubscriber(new StatsSubscriber() {
    @Override
    public void stats(StatsData stats) {
        System.out.printf("%s (%s)%n", stats.getMemory().getUsage(), stats.getMemory().getPercent());
    }
});
server.addHeapSubscriber(new HeapSubscriber() {
    @Override
    public void heap(HeapUsage heap) {
        System.out.println(heap.getUsage());
    }
});
```

#### Unsubscribe
You can unsubscribe from one, multiple or all streams using the server.unsubscribe() function.

```java
ExarotonClient client = new ExarotonClient("example-api-token");

Server server = client.getServer("tgkm731xO7GiHt76");
server.subscribe("console", "stats", "heap");
server.unsubscribe("heap");
server.unsubscribe("stats", "console");
server.unsubscribe(); // closes websocket connection
```

### Debugging Websocket connections
```java
ExarotonClient client = new ExarotonClient("example-api-token");

Server server = client.getServer("tgkm731xO7GiHt76");
server.subscribe();
server.getWebSocket().setErrorListener((message, throwable) -> {
    System.out.println(message);
    System.out.println(throwable.toString());
});
server.getWebSocket().setDebugListener(System.out::println);
```
