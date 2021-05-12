import com.exaroton.api.ExarotonClient;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SetAPITokenTest {
    private static final ExarotonClient client = new ExarotonClient(System.getenv("EXAROTON_TOKEN"));

    @Test
    void apiToken() {
        assertDoesNotThrow(() -> client.setAPIToken(System.getenv("EXAROTON_TOKEN")));
    }

    @Test
    void apiTokenEmpty() {
        assertThrows(IllegalArgumentException.class, () -> client.setAPIToken(""));
    }

    @Test
    void apiTokenNull() {
        assertThrows(IllegalArgumentException.class, () -> client.setAPIToken(null));
    }
}
