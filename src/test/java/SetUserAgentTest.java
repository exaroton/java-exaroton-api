import com.exaroton.api.ExarotonClient;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SetUserAgentTest {
    private static final ExarotonClient client = new ExarotonClient(System.getenv("EXAROTON_API_TOKEN"));

    @Test
    void userAgent() {
        assertDoesNotThrow(() -> client.setUserAgent("java-exaroton-api-tests"));
    }

    @Test
    void userAgentEmpty() {
        assertThrows(IllegalArgumentException.class, () -> client.setUserAgent(""));
    }

    @Test
    void userAgentNull() {
        assertThrows(IllegalArgumentException.class, () -> client.setUserAgent(null));
    }
}
