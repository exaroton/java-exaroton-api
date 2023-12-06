import com.exaroton.api.APIException;
import com.exaroton.api.server.Server;
import com.exaroton.api.server.ServerFile;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class GetFileTest extends APIClientTest {
    @Test
    void getFile() {
        Server server = client.getServer(TEST_SERVER_ID);
        assertDoesNotThrow(() -> {
            ServerFile whitelist = server.getFile("whitelist.json");
            whitelist.putContent("[{\"name\":\"JulianVennen\", \"uuid\": \"abcd9e56-5ac2-490c-8bc9-6c1cad18f506\"}]");
            assertNotNull(whitelist);
            assertNotNull(whitelist.getInfo());
            assertFalse(whitelist.isConfigFile());
            assertTrue(whitelist.isTextFile());
            assertFalse(whitelist.isDirectory());
            assertFalse(whitelist.isLog());
            assertTrue(whitelist.isReadable());
            assertTrue(whitelist.isWritable());

            String content = whitelist.getContent();
            assertNotNull(content);

            Path path = Paths.get("whitelist.json");
            whitelist.download(path);
            try (FileInputStream input = new FileInputStream("whitelist.json")) {
                assertEquals(content,
                    new BufferedReader(new InputStreamReader(input)).lines().collect(Collectors.joining("\n")));
            }

            whitelist.delete();
            assertThrows(APIException.class, whitelist::getInfo);
            whitelist.putContent("[]");
            assertEquals("[]", whitelist.getContent());

            whitelist.upload(path);
            assertEquals(content, whitelist.getContent());

            Files.delete(path);
        });
    }
}
