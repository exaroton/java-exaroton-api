import com.exaroton.api.Page;
import com.exaroton.api.billing.transactions.Transaction;
import com.exaroton.api.server.*;
import com.exaroton.api.server.access.ServerAccess;
import com.exaroton.api.server.backups.*;
import com.exaroton.api.server.domains.Domain;
import com.exaroton.api.server.domains.DomainProtocol;
import com.exaroton.api.server.domains.DomainStatus;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DeserializationTest {
    private final Gson gson = new Gson();

    @Test
    void testRegion() {
        List<Region> regions = gson.fromJson("[{\"id\":\"eu\",\"name\":\"Europe\"},{\"id\":\"xx\",\"name\":\"New\"}]",
                new TypeToken<List<Region>>(){}.getType());
        assertEquals("eu", regions.get(0).getId());
        assertEquals("Europe", regions.get(0).getName());
        assertEquals(ServerRegion.EU, regions.get(0).getServerRegion().orElse(null));
        assertTrue(regions.get(1).getServerRegion().isEmpty());
        assertEquals("na", ServerRegion.NA.getId());
    }

    @Test
    void testServerRegion() {
        Server server = gson.fromJson("{\"id\":\"tgkm731xO7GiHt76\",\"region\":\"na\"}", Server.class);
        assertEquals("na", server.getRegion());
    }

    @Test
    void testServerProperties() {
        ServerProperties properties = gson.fromJson("{\"autostop\":true,\"autostop-time\":10," +
                "\"autostart-type\":\"ops\",\"auto-update\":\"ask\",\"enable-lobby\":false}", ServerProperties.class);
        assertEquals(true, properties.getAutostop().orElse(null));
        assertEquals(10, properties.getAutostopTime().orElse(null));
        assertEquals(AutostartType.OPS, properties.getAutostartType().orElse(null));
        assertEquals(AutoUpdateMode.ASK, properties.getAutoUpdate().orElse(null));
        assertEquals(false, properties.getEnableLobby().orElse(null));
        assertTrue(properties.getAutostart().isEmpty());
        assertTrue(properties.getForceUpgrade().isEmpty());
    }

    @Test
    void testBackupPage() {
        BackupPage page = gson.fromJson("{\"items\":[{\"id\":\"Xy7zAb3dEf6hIj9k\",\"name\":null,\"type\":\"manual\"," +
                "\"status\":\"creating\",\"server\":\"EwYiY9IAMtQBTb6U\",\"software\":null,\"size\":52428800," +
                "\"created\":1763251200,\"backupAccount\":\"Ab3dEf6hIj9kLm2n\"}],\"page\":1,\"lastPage\":2," +
                "\"total\":21,\"running\":true}", BackupPage.class);
        assertTrue(page.isRunning());
        assertEquals(1, page.getPage());
        assertEquals(2, page.getLastPage());
        assertEquals(21, page.getTotal());
        assertTrue(page.hasNextPage());

        Backup backup = page.getItems().get(0);
        assertEquals("Xy7zAb3dEf6hIj9k", backup.getId());
        assertTrue(backup.getName().isEmpty());
        assertEquals(BackupType.MANUAL, backup.getType().orElse(null));
        assertEquals(BackupStatus.CREATING, backup.getStatus());
        assertEquals("EwYiY9IAMtQBTb6U", backup.getServerId());
        assertTrue(backup.getSoftwareId().isEmpty());
        assertEquals(52428800L, backup.getSize().orElse(null));
        assertEquals(Instant.ofEpochSecond(1763251200), backup.getCreated().orElse(null));
        assertEquals("Ab3dEf6hIj9kLm2n", backup.getBackupAccountId().orElse(null));
    }

    @Test
    void testEmptyPage() {
        Page<Transaction> page = gson.fromJson("{\"items\":[],\"page\":5,\"lastPage\":1,\"total\":0}",
                new TypeToken<Page<Transaction>>(){}.getType());
        assertTrue(page.getItems().isEmpty());
        assertFalse(page.hasNextPage());
        assertFalse(page.iterator().hasNext());
    }

    @Test
    void testBackupAccount() {
        BackupAccount account = gson.fromJson("{\"id\":\"Ab3dEf6hIj9kLm2n\",\"name\":\"example@gmail.com\"," +
                "\"provider\":\"google-drive\",\"storage\":{\"used\":1073741824,\"limit\":null}}", BackupAccount.class);
        assertEquals("google-drive", account.getProvider());
        assertEquals(1073741824L, account.getUsedStorage().orElse(null));
        assertTrue(account.getStorageLimit().isEmpty());
    }

    @Test
    void testBackupSettings() {
        BackupSettings settings = gson.fromJson("{\"enabled\":true,\"limit\":5,\"interval\":86400,\"failed\":1}",
                BackupSettings.class);
        assertTrue(settings.isEnabled());
        assertEquals(5, settings.getLimit());
        assertEquals(Duration.ofDays(1), settings.getInterval());
        assertEquals(1, settings.getFailed());
    }

    @Test
    void testDomain() {
        Domain domain = gson.fromJson("{\"id\":\"Pq4rSt7uVw0xYz3a\",\"domain\":\"play.example.com\"," +
                "\"status\":\"linked\",\"protocol\":\"https\",\"port\":80,\"checkedTime\":null," +
                "\"createdTime\":1763251200,\"nameservers\":[\"galileo.exaroton.com\",\"columbus.exaroton.com\"]}",
                Domain.class);
        assertEquals("play.example.com", domain.getDomain());
        assertEquals(DomainStatus.LINKED, domain.getStatus());
        assertEquals(DomainProtocol.HTTPS, domain.getProtocol());
        assertEquals(80, domain.getPort());
        assertTrue(domain.getCheckedTime().isEmpty());
        assertEquals(Instant.ofEpochSecond(1763251200), domain.getCreatedTime().orElse(null));
        assertEquals(List.of("galileo.exaroton.com", "columbus.exaroton.com"), domain.getNameservers());
    }

    @Test
    void testAccess() {
        ServerAccess access = gson.fromJson("{\"id\":\"Hj8kLm2nPq4rSt7u\",\"account\":{\"id\":\"EwYiY9IAMtQBTb6s\"," +
                "\"name\":\"example\"},\"permissions\":[\"server\",\"stop\"]}", ServerAccess.class);
        assertEquals("Hj8kLm2nPq4rSt7u", access.getId());
        assertEquals("EwYiY9IAMtQBTb6s", access.getAccount().getId());
        assertEquals("example", access.getAccount().getName());
        assertEquals(List.of("server", "stop"), access.getPermissions());
    }

    @Test
    void testTransaction() {
        Page<Transaction> page = gson.fromJson("{\"items\":[{\"id\":\"Tr4nS4cT10nId123\",\"type\":\"run\"," +
                "\"credits\":-1.5,\"rate\":1,\"interval\":3600,\"amount\":4,\"active\":false,\"paid\":true," +
                "\"startTime\":1763247600,\"endTime\":1763251200,\"account\":\"EwYiY9IAMtQBTb6s\",\"pool\":null," +
                "\"server\":\"EwYiY9IAMtQBTb6U\"}],\"page\":1,\"lastPage\":1,\"total\":1}",
                new TypeToken<Page<Transaction>>(){}.getType());
        Transaction transaction = page.iterator().next();
        assertEquals("run", transaction.getType());
        assertEquals(-1.5, transaction.getCredits());
        assertEquals(1, transaction.getRate().orElse(null));
        assertEquals(Duration.ofHours(1), transaction.getInterval().orElse(null));
        assertEquals(4.0, transaction.getAmount().orElse(null));
        assertFalse(transaction.isActive());
        assertTrue(transaction.isPaid());
        assertEquals(Instant.ofEpochSecond(1763247600), transaction.getStartTime());
        assertEquals(Instant.ofEpochSecond(1763251200), transaction.getEndTime().orElse(null));
        assertEquals("EwYiY9IAMtQBTb6s", transaction.getAccount().orElse(null));
        assertTrue(transaction.getPool().isEmpty());
        assertEquals("EwYiY9IAMtQBTb6U", transaction.getServer().orElse(null));
    }
}
