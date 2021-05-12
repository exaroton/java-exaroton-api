## Java exaroton API Client

---
### About
The official java library for the [exaroton API](https://support.exaroton.com/hc/en-us/articles/360019857878-API-documentation) 
that can be used to automatically manage minecraft servers (e.g. starting or stopping it).

While exaroton also offers a web-socket API, this library currently only supports the REST API.

Minimum Java Version: 8

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
        ExarotonClient client = new ExarotonClient("asd");

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
        ExarotonClient client = new ExarotonClient("asd");

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
        ExarotonClient client = new ExarotonClient("asd");

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
        ExarotonClient client = new ExarotonClient("asd");

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
        ExarotonClient client = new ExarotonClient("asd");

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
        ExarotonClient client = new ExarotonClient("asd");

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
