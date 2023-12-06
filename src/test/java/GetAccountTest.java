import com.exaroton.api.account.Account;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GetAccountTest extends APIClientTest {
    @Test
    void getAccount() {
        assertDoesNotThrow(() -> {
            Account a = client.getAccount();
            assertNotNull(a.getName());
            assertNotNull(a.getEmail());
        });
    }
}
