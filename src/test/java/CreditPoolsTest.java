import com.exaroton.api.APIException;
import com.exaroton.api.billing.pools.CreditPool;
import com.exaroton.api.billing.pools.CreditPoolMember;
import com.exaroton.api.server.Server;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Optional;

public class CreditPoolsTest extends APIClientTest {
    private static final @NotNull String TEST_POOL_ID = System.getenv("EXAROTON_TEST_POOL");

    @Test
    public void testGetCreditPools() throws APIException {
        CreditPool[] pools = client.getCreditPools();
        Assertions.assertNotNull(pools);
        Assertions.assertTrue(pools.length > 0);
        checkTestPool(Arrays.stream(pools).filter(pool -> pool.getId().equals(TEST_POOL_ID)).findFirst().orElse(null));
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
    public void testGetPool() throws APIException {
        CreditPool pool = client.getCreditPool(TEST_POOL_ID);
        pool.get();
        checkTestPool(pool);
    }

    @Test
    public void testGetPoolMembers() throws APIException {
        CreditPool pool = client.getCreditPool(TEST_POOL_ID);
        CreditPoolMember[] members = pool.getMemberList();
        Assertions.assertNotNull(members);
        Assertions.assertEquals(2, members.length);

        Optional<CreditPoolMember> owner = Arrays.stream(members).filter(CreditPoolMember::isOwner).findFirst();
        Assertions.assertTrue(owner.isPresent());
        Assertions.assertNotNull(owner.get().getAccount());
        Assertions.assertTrue(owner.get().isOwner());
        Assertions.assertTrue(owner.get().getCredits() > 100, "Expected owner to have more than 100 credits, got " + owner.get().getCredits());
        Assertions.assertEquals(1, owner.get().getShare());

        Optional<CreditPoolMember> other = Arrays.stream(members).filter(member -> !member.isOwner()).findFirst();
        Assertions.assertTrue(other.isPresent());
        Assertions.assertNotNull(other.get().getAccount());
        Assertions.assertFalse(other.get().isOwner());
        Assertions.assertEquals(0, other.get().getCredits());
        Assertions.assertEquals(0, other.get().getShare());
    }

    @Test
    public void testGetPoolServers() throws APIException {
        CreditPool pool = client.getCreditPool(TEST_POOL_ID);
        Server[] servers = pool.getServerList();
        Assertions.assertNotNull(servers);
        Assertions.assertEquals(1, servers.length);
        Assertions.assertEquals(TEST_SERVER_ID, servers[0].getId());
    }
}
