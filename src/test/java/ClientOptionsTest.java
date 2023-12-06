import com.exaroton.api.UnsupportedProtocolException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    void protocolHTTP() {
        assertDoesNotThrow(() -> client.setProtocol("http"));
        assertDoesNotThrow(() -> client.setProtocol("HTTP"));
    }

    @Test
    void protocolHTTPS() {
        assertDoesNotThrow(() -> client.setProtocol("https"));
        assertDoesNotThrow(() -> client.setProtocol("HTTPS"));
    }

    @Test
    void protocolFTP() {
        assertThrows(UnsupportedProtocolException.class, () -> client.setProtocol("ftp"));
    }

    @Test
    void protocolEmpty() {
        assertThrows(UnsupportedProtocolException.class, () -> client.setProtocol(""));
    }

    @Test
    void protocolNull() {
        assertThrows(IllegalArgumentException.class, () -> client.setProtocol(null));
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
