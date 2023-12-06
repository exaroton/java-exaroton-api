import com.exaroton.api.APIException;
import com.exaroton.api.server.PlayerList;
import com.exaroton.api.server.Server;
import com.exaroton.api.server.ServerLog;
import com.exaroton.api.server.ServerMOTDInfo;
import com.exaroton.api.server.ServerRAMInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ServerTest extends APIClientTest {
    @Test
    void testGetServers() throws APIException {
        Server[] servers = client.getServers();
        assertNotNull(servers);
        for (Server server: servers) {
            assertNotNull(server.getAddress());
            assertNotNull(server.getName());
            assertNotNull(server.getId());
        }
    }

    @Test
    void testGetServer() throws APIException {
        server.get();
    }

    @Test
    void testGetLog() throws APIException {
        ServerLog log = server.getLog();
        assertNotNull(log);
        assertNotNull(log.getContent());
    }

    @Test
    void testGetMotd() throws APIException {
        assertNull(server.getMotd());
        ServerMOTDInfo fetched = server.fetchMotd();
        assertNotNull(fetched);
        assertNotNull(server.getMotd());
        assertEquals(fetched.getMotd(), server.getMotd());
    }

    @Test
    void testGetRAM() throws APIException {
        ServerRAMInfo ram = server.getRAM();
        assertNotNull(ram);
        assertTrue(ram.getRam() > 0);
    }

    @Test
    void testGetPlayerLists() throws APIException {
        String[] lists = server.getPlayerLists();
        assertNotNull(lists);
        assertTrue(lists.length > 0);

        for (String name: lists) {
            assertNotNull(name);
            PlayerList list = server.getPlayerList(name);
            assertNotNull(list);
            assertNotNull(list.getName());
            assertNotNull(list.getEntries());
        }
    }
}
