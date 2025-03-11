import com.exaroton.api.server.PlayerList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerListTest extends APIClientTest {
    protected PlayerList whitelist;

    @Override
    @BeforeEach
    void setUp() {
        super.setUp();
        whitelist = server.getPlayerList("whitelist");
    }

    @Test
    void testGetPlayerLists() throws IOException {
        List<String> lists = server.getPlayerLists().join();
        assertNotNull(lists);
        assertFalse(lists.isEmpty(), "Expected at least one player list, got none");

        for (String name : lists) {
            assertNotNull(name);
            PlayerList list = server.getPlayerList(name);
            assertNotNull(list);
            assertNotNull(list.getName());
            assertNotNull(list.getEntries());
        }
    }

    @Test
    void testGetEntries() throws IOException {
        List<String> entries = whitelist.getEntries().join();
        assertNotNull(entries);
        assertTrue(entries.contains("Aternos"), "Expected 'Aternos' to be in whitelist");
    }

    @Test
    void testAddRemoveEntries() throws IOException {
        List<String> entries = whitelist.add("exaroton").join();
        assertNotNull(entries);
        assertTrue(entries.contains("exaroton"), "Expected 'exaroton' to be in whitelist");

        entries = whitelist.remove("exaroton").join();
        assertNotNull(entries);
        assertFalse(entries.contains("exaroton"), "Expected 'exaroton' not to be in whitelist");
    }

    @Test
    void testRemoveEmpty() {
        assertThrows(IllegalArgumentException.class, () -> whitelist.remove());
        assertThrows(IllegalArgumentException.class, () -> whitelist.remove(List.of()));
    }

    @Test
    void testAddEmpty() {
        assertThrows(IllegalArgumentException.class, () -> whitelist.add());
        assertThrows(IllegalArgumentException.class, () -> whitelist.add(List.of()));
    }
}
