import com.exaroton.api.account.Account;
import com.exaroton.api.ExarotonClient;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GetAccountTest {
    private static final ExarotonClient client = new ExarotonClient(System.getenv("EXAROTON_TOKEN"));

    @Test
    void getAccount() {
        assertDoesNotThrow(() -> {
            Account a = client.getAccount();
            assertNotNull(a.getName());
            assertNotNull(a.getEmail());
        });
    }
}
