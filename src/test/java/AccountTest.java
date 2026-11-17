import com.exaroton.api.Page;
import com.exaroton.api.billing.transactions.Transaction;
import com.exaroton.api.server.Region;
import com.exaroton.api.server.ServerRegion;
import com.exaroton.api.APIException;
import com.exaroton.api.account.Account;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AccountTest extends APIClientTest {
    @Test
    void getAccount() throws IOException {
        Account a = client.getAccount().join();
        assertNotNull(a.getName());
        assertNotNull(a.getEmail());
        assertTrue(a.getVerified());
        assertTrue(a.getCredits() > 0);
        assertNotNull(a.getId());
        assertEquals(16, a.getId().length());
        assertTrue(a.getMaxServers() > 0);
    }

    @Test
    void getAccountTransactions() throws IOException {
        Page<Transaction> page = client.getAccountTransactions().join();
        assertEquals(1, page.getPage());
        assertTrue(page.getLastPage() >= 1);
        for (Transaction transaction : page) {
            assertNotNull(transaction.getId());
            assertNotNull(transaction.getType());
        }

        Page<Transaction> empty = client.getAccountTransactions(page.getLastPage() + 1).join();
        assertTrue(empty.getItems().isEmpty());
    }

    @Test
    void getRegions() throws IOException {
        List<Region> regions = client.getRegions().join();
        assertFalse(regions.isEmpty());
        for (ServerRegion region : ServerRegion.values()) {
            assertTrue(regions.stream().anyMatch(r -> r.getId().equals(region.getId())),
                    "Expected region " + region.getId());
        }
    }
}
