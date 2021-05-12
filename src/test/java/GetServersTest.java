import com.exaroton.api.ExarotonClient;
import com.exaroton.api.server.Server;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GetServersTest {
    private static final ExarotonClient client = new ExarotonClient(System.getenv("EXAROTON_TOKEN"));

    @Test
    void getServers() {
        assertDoesNotThrow(() -> {
            Server[] servers = client.getServers();
            assertNotNull(servers);
            for (Server server: servers) {
                assertNotNull(server.getAddress());
                assertNotNull(server.getName());
                assertNotNull(server.getId());

                assertDoesNotThrow(() -> {
                    Server s = client.getServer(server.getId()).get();
                    assertEquals(s.getId(), server.getId());
                    assertEquals(s.getAddress(), server.getAddress());
                    assertEquals(s.getName(), server.getName());
                    assertDoesNotThrow(server::getLog);
                    assertDoesNotThrow(server::getRAM);
                    assertDoesNotThrow(server::getPlayerLists);
                });
            }
        });
    }
}
