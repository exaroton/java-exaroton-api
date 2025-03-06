import com.exaroton.api.APIException;
import com.exaroton.api.server.PlayerList;
import com.exaroton.api.server.Server;
import com.exaroton.api.server.ServerLog;
import com.exaroton.api.server.ServerMOTDInfo;
import com.exaroton.api.server.ServerRAMInfo;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

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
    void testGetServer() throws IOException {
        server.get();
    }

    @Test
    void testGetLog() throws IOException {
        ServerLog log = server.getLog().join();
        assertNotNull(log);
        assertNotNull(log.getContent());
    }

    @Test
    void testGetMotd() throws IOException {
        assertNull(server.getMotd());
        ServerMOTDInfo fetched = server.fetchMotd().join();
        assertNotNull(fetched);
        assertNotNull(server.getMotd());
        assertEquals(fetched.getMotd(), server.getMotd());
    }

    @Test
    void testGetRAM() throws IOException {
        ServerRAMInfo ram = server.getRAM().join();
        assertNotNull(ram);
        assertTrue(ram.getRam() > 0);
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
}
