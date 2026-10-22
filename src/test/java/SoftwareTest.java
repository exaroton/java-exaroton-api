import com.exaroton.api.server.ServerSoftware;
import com.exaroton.api.server.ServerSoftwareDetails;
import com.exaroton.api.software.Software;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SoftwareTest extends APIClientTest {
    @Test
    void testGetSoftwareList() throws IOException {
        List<Software> software = client.getSoftwareList().join();
        assertNotNull(software);
        assertFalse(software.isEmpty());
        for (Software s : software) {
            assertNotNull(s.getId());
            assertNotNull(s.getName());
            assertNotNull(s.getVersion());
            assertNotNull(s.getType());
        }
    }

    @Test
    void testGetSoftwareListFiltered() throws IOException {
        List<Software> software = client.getSoftwareList("vanilla").join();
        assertNotNull(software);
        assertFalse(software.isEmpty());
        for (Software s : software) {
            assertEquals("vanilla", s.getType());
        }
    }

    @Test
    void testGetServerSoftwareDetails() throws IOException {
        ServerSoftware installed = server.fetch().join().getSoftware();
        assertNotNull(installed);
        assertNotNull(installed.getType());

        ServerSoftwareDetails details = server.getSoftwareDetails(installed.getId()).join();
        assertNotNull(details);
        assertEquals(installed.getId(), details.getId());
        assertEquals(installed.getName(), details.getName());
        assertEquals(installed.getVersion(), details.getVersion());
        assertNotNull(details.getAddons());
        assertFalse(details.requiresReinstall());
        assertFalse(details.supportsAddon("not-an-addon-type"));
    }

    @Test
    void testGetServerSoftwareDetailsInvalidId() {
        assertThrows(IllegalArgumentException.class, () -> server.getSoftwareDetails("invalid"));
    }

    @Test
    void testInstallSoftwareInvalidId() {
        assertThrows(IllegalArgumentException.class, () -> server.installSoftware("invalid"));
    }
}
