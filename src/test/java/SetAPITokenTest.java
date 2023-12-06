import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SetAPITokenTest extends APIClientTest {
    @Test
    void apiToken() {
        assertDoesNotThrow(() -> client.setAPIToken(System.getenv("EXAROTON_API_TOKEN")));
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
