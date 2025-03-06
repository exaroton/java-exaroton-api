import com.exaroton.api.billing.pools.CreditPool;
import com.exaroton.api.billing.pools.CreditPoolMember;
import com.exaroton.api.server.Server;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class CreditPoolsTest extends APIClientTest {
    private static final @NotNull String TEST_POOL_ID = System.getenv("EXAROTON_TEST_POOL");

    @Test
    public void testGetCreditPools() throws IOException {
        List<CreditPool> pools = client.getCreditPools().join();
        Assertions.assertNotNull(pools);
        Assertions.assertFalse(pools.isEmpty(), "Expected at least one pool, got none");
        checkTestPool(pools.stream().filter(pool -> pool.getId().equals(TEST_POOL_ID)).findFirst().orElse(null));
    }

    private void checkTestPool(CreditPool pool) {
        Assertions.assertNotNull(pool);
        Assertions.assertEquals(TEST_POOL_ID, pool.getId());
        Assertions.assertEquals("eAPI tests", pool.getName());
        double poolCredits = pool.getCredits();
        Assertions.assertTrue(poolCredits > 100, "Expected pool to have more than 100 credits, got " + poolCredits);
        Assertions.assertEquals(poolCredits, pool.getOwnCredits());
        Assertions.assertEquals(2, pool.getMembers());
        Assertions.assertEquals(1, pool.getServers());
    }

    @Test
    public void testGetPool() throws IOException {
        CreditPool pool = client.getCreditPool(TEST_POOL_ID);
        checkTestPool(pool.fetch().join());
        checkTestPool(pool);
    }

    @Test
    public void testGetPoolCache() throws IOException {
        CreditPool pool = client.getCreditPool(TEST_POOL_ID);

        var future = pool.fetch(false);
        Assertions.assertFalse(future.isDone());
        Assertions.assertNull(future.getNow(null));

        checkTestPool(pool.fetch().join());
        checkTestPool(pool);

        future = pool.fetch(false);
        Assertions.assertTrue(future.isDone());
        checkTestPool(future.getNow(null));
    }

    @Test
    public void testGetPoolMembers() throws IOException {
        CreditPool pool = client.getCreditPool(TEST_POOL_ID);
        List<CreditPoolMember> members = pool.getMemberList().join();
        Assertions.assertNotNull(members);
        Assertions.assertEquals(2, members.size());

        Optional<CreditPoolMember> owner = members.stream().filter(CreditPoolMember::isOwner).findFirst();
        Assertions.assertTrue(owner.isPresent());
        Assertions.assertNotNull(owner.get().getAccount());
        Assertions.assertNotNull(owner.get().getName());
        Assertions.assertTrue(owner.get().isOwner());
        Assertions.assertTrue(owner.get().getCredits() > 100, "Expected owner to have more than 100 credits, got " + owner.get().getCredits());
        Assertions.assertEquals(1, owner.get().getShare());

        Optional<CreditPoolMember> other = members.stream().filter(member -> !member.isOwner()).findFirst();
        Assertions.assertTrue(other.isPresent());
        Assertions.assertNotNull(other.get().getAccount());
        Assertions.assertFalse(other.get().isOwner());
        Assertions.assertEquals(0, other.get().getCredits());
        Assertions.assertEquals(0, other.get().getShare());
    }

    @Test
    public void testGetPoolServers() throws IOException {
        CreditPool pool = client.getCreditPool(TEST_POOL_ID);
        List<Server> servers = pool.getServerList().join();
        Assertions.assertNotNull(servers);
        Assertions.assertEquals(1, servers.size());
        Assertions.assertEquals(TEST_SERVER_ID, servers.get(0).getId());
    }
}
