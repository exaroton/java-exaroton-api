import com.exaroton.api.APIException;
import com.exaroton.api.account.Account;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class AccountTest extends APIClientTest {
    @Test
    void getAccount() throws IOException {
        Account a = client.getAccount().join();
        assertNotNull(a.getName());
        assertNotNull(a.getEmail());
        assertTrue(a.getVerified());
        assertTrue(a.getCredits() > 0);
    }
}
