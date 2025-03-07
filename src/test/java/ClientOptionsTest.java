import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ClientOptionsTest extends APIClientTest {
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
