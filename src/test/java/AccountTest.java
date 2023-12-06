import com.exaroton.api.APIException;
import com.exaroton.api.account.Account;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AccountTest extends APIClientTest {
    @Test
    void getAccount() throws APIException {
        Account a = client.getAccount();
        assertNotNull(a.getName());
        assertNotNull(a.getEmail());
        assertTrue(a.getVerified());
        assertTrue(a.getCredits() > 0);
    }
}
