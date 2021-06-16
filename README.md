## Java exaroton API Client

---
### About
The official java library for the [exaroton API](https://support.exaroton.com/hc/en-us/articles/360019857878-API-documentation) 
that can be used to automatically manage Minecraft servers (e.g. starting or stopping them).

Required Java Version: 8+

If you're creating a plugin/mod that runs on an exaroton server, you can use the environment variable
EXAROTON_SERVER_ID to find the ID of the server it's running on.

### Installing
Gradle:
```gradle
dependencies {
    implementation 'com.exaroton:api:1.1.1'
}
```

Maven:
```xml
<dependency>
  <groupId>com.exaroton</groupId>
  <artifactId>api</artifactId>
  <version>1.1.1</version>
</dependency>
```

### Usage 
You need an API key to use the API. You can generate an api key in the [account options](https://exaroton.com/account/).


#### Create a client

```java
import com.exaroton.api.ExarotonClient;

public class Example {
    public static void main(String[] args) {
        ExarotonClient client = new ExarotonClient("example-api-token");
    }
}
```

### REST API

#### Show account info
```java
import com.exaroton.api.ExarotonClient;

public class Example {
    public static void main(String[] args) {
        ExarotonClient client = new ExarotonClient("example-api-token");
        try {
            Account account = client.getAccount();
            System.out.println("My account " + account.getName() + " has " + account.getCredits() + "!");
        } catch (APIException e) {
            e.printStackTrace();
        }
    }
}
```
Objects of the Account class contain getters for each field in the [API docs](https://support.exaroton.com/hc/en-us/articles/360011926177#account). 

#### Get all servers
```java
import com.exaroton.api.APIException;
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.server.Server;

public class Example {
    public static void main(String[] args) {
        ExarotonClient client = new ExarotonClient("example-api-token");

        try {
            Server[] servers = client.getServers();
            for (Server server: servers) {
                System.out.println(server.getId() + ":" + server.getAddress());
            }
        } catch (APIException e) {
            e.printStackTrace();
        }
    }
}
```
Objects of the Server class contain getters for each field in the [API docs](https://support.exaroton.com/hc/en-us/articles/360011926177#servers).


#### Get a single server
```java
import com.exaroton.api.APIException;
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.server.Server;

public class Example {
    public static void main(String[] args) {
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
    }
}
```

#### Check the server status
```java
import com.exaroton.api.APIException;
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.server.Server;
import com.exaroton.api.server.ServerStatus;

public class Example {
    public static void main(String[] args) {
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
    }
}
```
The server status is an integer as described in the [documentation](https://support.exaroton.com/hc/en-us/articles/360011926177#servers).
You can use the ServerStatus class to easily get the value of any status.

#### Get/Share your server log
```java
import com.exaroton.api.APIException;
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.server.MclogsData;
import com.exaroton.api.server.Server;
import com.exaroton.api.server.ServerLog;

public class Example {
    public static void main(String[] args) {
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
    }
}
```
The result is cached and will not return the latest updates immediately. It's not possible to get the server logs while the server is loading, stopping or saving.


#### Get/Set server RAM
```java
import com.exaroton.api.APIException;
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.server.Server;
import com.exaroton.api.server.ServerRAMInfo;

public class Example {
    public static void main(String[] args) {
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
    }
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
import com.exaroton.api.APIException;
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.server.PlayerList;
import com.exaroton.api.server.Server;

public class Example {
    public static void main(String[] args) {
        ExarotonClient client = new ExarotonClient("example-api-token");

        Server server = client.getServer("tgkm731xO7GiHt76");
        PlayerList whitelist = server.getPlayerList("whitelist");
        try {
            System.out.println("Whitelist:");
            for (String entry: whitelist.getEntries()) {
                System.out.println(entry);
            }
            whitelist.add(new String[]{"example", "example2"});
            whitelist.remove("example34");
        } catch (APIException e) {
            e.printStackTrace();
        }
    }
}
```

### Websocket API
The websocket API allows a constant connection to our websocket service to receive events in real time without polling 
(e.g. trying to get the server status every few seconds).

#### Server status changes
You can simply connect to the websocket API for a server by running the subscribe() function.
By default, you are always subscribed to server status update events, you can react to server status 
changes by adding a subscriber: 

```java
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.server.Server;
import com.exaroton.api.ws.subscriber.ServerStatusSubscriber;

public class Example {
    public static void main(String[] args) {
        ExarotonClient client = new ExarotonClient("example-api-token");

        Server server = client.getServer("tgkm731xO7GiHt76");
        server.subscribe();
        server.addStatusSubscriber(new ServerStatusSubscriber() {
            @Override
            public void statusUpdate(Server oldServer, Server newServer) {
                System.out.printf("Server had status %s and now has status %s!%n", oldServer.getStatus(), newServer.getStatus());
            }
        });
    }
}
```
This event is not only triggered when the status itself changes but also when other events happen, 
e.g. a player joins the server.

#### Console messages
One of the optional streams is the console stream. You can subscribe to one or more optional streams 
using the subscribe method. The console stream emits an event for every new console line.
```java
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.server.Server;
import com.exaroton.api.ws.subscriber.ConsoleSubscriber;

public class Example {
    public static void main(String[] args) {
        ExarotonClient client = new ExarotonClient("example-api-token");

        Server server = client.getServer("tgkm731xO7GiHt76");
        server.subscribe("console");
        server.addConsoleSubscriber(new ConsoleSubscriber() {
            @Override
            public void line(String line) {
                System.out.println(line);
            }
        });
    }
}
```

The console stream also allows you to send commands directly over the websocket. This is faster because
the connection is already established and no further authorization etc. is necessary. This library
already checks if you are subscribed to the console stream and sends the command through that stream
instead, so you can just use it the same way as [before](#get-a-single-server).

#### Tick times
On Minecraft Java edition servers with version 1.16 and higher it is possible to get the tick times, 
and the TPS (ticks per second) of your server. This information is also available as an optional stream.

```java
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.server.Server;
import com.exaroton.api.ws.data.TickData;
import com.exaroton.api.ws.subscriber.TickSubscriber;

public class Example {
    public static void main(String[] args) {
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
    }
}
```
The tps are calculated by dividing 1000 by the average tick time and limiting it to 20.

#### RAM usage
There are two different optional streams to get RAM usage, the general stats stream and the Java specific 
heap stream. It is recommended to use the heap stream if you are running a server software that is based 
on Java. It is not recommended using both.

You can subscribe to multiple streams at once by passing an array to the subscribe function.

```java
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.server.Server;
import com.exaroton.api.ws.data.HeapUsage;
import com.exaroton.api.ws.data.StatsData;
import com.exaroton.api.ws.subscriber.HeapSubscriber;
import com.exaroton.api.ws.subscriber.StatsSubscriber;

public class Example {
    public static void main(String[] args) {
        ExarotonClient client = new ExarotonClient("example-api-token");

        Server server = client.getServer("tgkm731xO7GiHt76");
        server.subscribe(new String[]{"stats", "heap"});
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
    }
}
```

#### Unsubscribe
You can unsubscribe from one, multiple or all streams using the server.unsubscribe() function.

```java
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.server.Server;

public class Example {
    public static void main(String[] args) {
        ExarotonClient client = new ExarotonClient("example-api-token");

        Server server = client.getServer("tgkm731xO7GiHt76");
        server.subscribe(new String[]{"console", "stats", "heap"});
        server.unsubscribe("heap");
        server.unsubscribe(new String[]{"stats", "console"});
        server.unsubscribe(); // closes websocket connection
    }
}
```