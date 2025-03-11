import com.exaroton.api.server.*;
import com.exaroton.api.ws.WaitForStatusSubscriber;
import com.exaroton.api.ws.subscriber.ConsoleSubscriber;
import com.exaroton.api.ws.subscriber.ServerStatusSubscriber;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

public class ServerTest extends APIClientTest {
    @Test
    void testGetServers() throws IOException {
        List<Server> servers = client.getServers().join();
        assertNotNull(servers);
        for (Server server: servers) {
            assertNotNull(server.getAddress());
            assertNotNull(server.getName());
            assertNotNull(server.getId());
        }
    }

    @Test
    void testFetchServer() throws IOException {
        assertFalse(server.isFetched());
        Server res = server.fetch().join();
        assertSame(res, server);
        checkTestServer();
    }

    private void checkTestServer() {
        assertEquals("tests4ET.exaroton.me", server.getAddress());
        assertEquals("tests4ET", server.getName());
        assertNotNull(server.getMotd());
        assertEquals(ServerStatus.OFFLINE, server.getStatus());
        assertTrue(server.hasStatus(ServerStatus.OFFLINE), "Expected server to be offline");
        assertTrue(server.hasStatus(ServerStatus.OFFLINE, ServerStatus.TRANSFERRING), "Expected server to be offline or transferring");
        assertFalse(server.hasStatus(ServerStatus.TRANSFERRING), "Expected server not to be transferring");

        var players = server.getPlayerInfo();
        assertNotNull(players);
        assertTrue(players.getList().isEmpty());
        assertEquals(0, players.getCount());
        assertEquals(20, players.getMax());

        assertTrue(server.getHost().isEmpty());
        assertEquals(52892, server.getPort().orElse(null));

        var software = server.getSoftware();
        assertNotNull(software);
        assertEquals("Vanilla", software.getName());
        assertNotNull(software.getId());
        assertNotNull(software.getVersion());
        assertTrue(software.getVersion().contains("1."));

        assertFalse(server.isShared());
        assertSame(client, server.getClient());
    }

    @Test
    void testFetchServerCached() throws IOException {
        assertFalse(server.isFetched());
        Server res = server.fetch(false).join();
        assertSame(res, server);
        checkTestServer();

        // fetch again, should be cached
        CompletableFuture<Server> future = server.fetch(false);
        assertTrue(future.isDone());
        Server res2 = future.getNow(null);
        assertNotNull(res2);
        assertSame(res2, server);
        checkTestServer();
    }

    @Test
    void testGetLog() throws IOException {
        ServerLog log = server.getLog().join();
        assertNotNull(log);
        assertNotNull(log.getContent());
    }

    @Test
    void testShareLog() throws IOException {
        var log = server.shareLog().join();
        assertNotNull(log);
        assertNotNull(log.getId());
        assertNotNull(log.getUrl());
        assertTrue(log.getUrl().contains("mclo.gs"));
        assertNotNull(log.getRaw());
        assertTrue(log.getRaw().contains("mclo.gs"));
    }

    @Test
    void testGetSetMotd() throws IOException {
        assertNull(server.getMotd());
        ServerMOTDInfo fetched = server.fetchMotd().join();
        assertNotNull(fetched);
        assertNotNull(server.getMotd());
        assertEquals(fetched.getMotd(), server.getMotd());
        assertNotEquals("test", server.getMotd());

        server.setMotd("test").join();
        assertEquals("test", server.getMotd());
        server.setMotd("§7Welcome to the server of §atest§7!").join();
    }

    @Test
    void testGetSetRAM() throws IOException {
        ServerRAMInfo ram = server.getRAM().join();
        assertNotNull(ram);
        assertTrue(ram.getRam() > 0);
        assertNotEquals(16, ram.getRam());

        server.setRAM(16).join();
        assertEquals(16, server.getRAM().join().getRam());
        server.setRAM(ram.getRam()).join();
    }

    @Test
    void testGetPlayerLists() throws IOException {
        List<String> lists = server.getPlayerLists().join();
        assertNotNull(lists);
        assertFalse(lists.isEmpty(), "Expected at least one player list, got none");

        for (String name: lists) {
            assertNotNull(name);
            PlayerList list = server.getPlayerList(name);
            assertNotNull(list);
            assertNotNull(list.getName());
            assertNotNull(list.getEntries());
        }
    }

    @Test
    void testRemoveSubscribersWithoutWebsocket() {
        server.removeStatusSubscriber((oldServer, newServer) -> {});
        server.removeConsoleSubscriber(x -> {});
        server.removeHeapSubscriber(x -> {});
        server.removeStatsSubscriber(x -> {});
        server.removeTickSubscriber(x -> {});
    }

    /**
     * Perform a series of tests that require the server to be online
     */
    @Test
    void testStartServer() throws IOException, ExecutionException, InterruptedException, TimeoutException {
        server.fetch().join();

        startServer();
        testExecuteCommand();
        restartServer();
        stopServer();

        assertNull(server.getWebSocket());
    }

    void testExecuteCommand() throws IOException, ExecutionException, InterruptedException, TimeoutException {
        var flagA = new FlagSubscriber();
        var flagB = new FlagSubscriber();

        server.addConsoleSubscriber(flagA);
        server.addConsoleSubscriber(flagB);

        server.executeCommand("say " + flagA.value).join();

        Server serverWithoutWS = client.getServer(TEST_SERVER_ID);
        serverWithoutWS.executeCommand("say " + flagB.value).join();

        flagA.future.get(10, TimeUnit.SECONDS);
        flagB.future.get(10, TimeUnit.SECONDS);

        server.removeConsoleSubscriber(flagA);
        server.removeConsoleSubscriber(flagB);
    }

    void startServer() throws IOException, ExecutionException, InterruptedException, TimeoutException {
        assertTrue(server.hasStatus(ServerStatus.GROUP_OFFLINE));

        AtomicBoolean receivedStatusUpdate = new AtomicBoolean(false);
        var statusSubscriber = new ServerStatusSubscriber() {
            @Override
            public void handleStatusUpdate(Server oldServer, Server newServer) {
                receivedStatusUpdate.set(true);
                assertSame(server, newServer);
            }
        };
        server.addStatusSubscriber(statusSubscriber);

        server.start().join();
        server.waitForStatus(ServerStatus.ONLINE).get(3, TimeUnit.MINUTES);
        assertEquals(ServerStatus.ONLINE, server.getStatus());

        server.removeStatusSubscriber(statusSubscriber);
        assertTrue(receivedStatusUpdate.get());
    }

    void restartServer() throws IOException, ExecutionException, InterruptedException, TimeoutException {
        assertEquals(ServerStatus.ONLINE, server.getStatus());

        var restartingFuture = server.waitForStatus(ServerStatus.RESTARTING);
        assertNotNull(server.getWebSocket());
        server.getWebSocket().waitForReady().get(1, TimeUnit.MINUTES);

        server.restart().join();
        restartingFuture.get(1, TimeUnit.MINUTES);
        assertEquals(ServerStatus.RESTARTING, server.getStatus());


        server.waitForStatus(ServerStatus.ONLINE).get(3, TimeUnit.MINUTES);
        assertEquals(ServerStatus.ONLINE, server.getStatus());
    }

    void stopServer() throws IOException, ExecutionException, InterruptedException, TimeoutException {
        assertFalse(server.hasStatus(ServerStatus.GROUP_OFFLINE));
        assertFalse(server.hasStatus(ServerStatus.GROUP_STOPPING));

        server.stop().join();
        server.waitForStatus(ServerStatus.GROUP_OFFLINE).get(3, TimeUnit.MINUTES);
        assertTrue(server.hasStatus(ServerStatus.GROUP_OFFLINE), "Expected server to be offline or crashed");
    }

    private static class FlagSubscriber implements ConsoleSubscriber {
        final String value;
        final CompletableFuture<Void> future = new CompletableFuture<>();

        public FlagSubscriber() {
            this.value = "exaroton-api-tests-" + new Random().nextInt();
        }

        @Override
        public void handleLine(String message) {
            if (message.contains(value)) {
                future.complete(null);
            }
        }
    }
}
