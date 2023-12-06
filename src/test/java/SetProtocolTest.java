import com.exaroton.api.UnsupportedProtocolException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SetProtocolTest extends APIClientTest {
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
}
