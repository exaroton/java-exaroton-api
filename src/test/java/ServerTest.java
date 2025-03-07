import com.exaroton.api.server.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

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
    void testStartServer() throws IOException {
        assertEquals(ServerStatus.OFFLINE, server.getStatus());
        server.start().join();
    }
}
