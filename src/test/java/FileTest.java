import com.exaroton.api.APIException;
import com.exaroton.api.server.ServerFile;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class FileTest extends APIClientTest {
    @Test
    void getFile() throws IOException {
        ServerFile whitelist = server.getFile("whitelist.json");
        whitelist.putContent("[{\"name\":\"Aternos\", \"uuid\": \"d6a91995-04bf-4f11-823f-5b18d412062a\"}]").join();
        assertNotNull(whitelist);
        assertNotNull(whitelist.fetch().join());
        assertFalse(whitelist.isConfigFile());
        assertTrue(whitelist.isTextFile());
        assertFalse(whitelist.isDirectory());
        assertFalse(whitelist.isLog());
        assertTrue(whitelist.isReadable());
        assertTrue(whitelist.isWritable());

        String content = whitelist.getContent().join();
        assertNotNull(content);

        Path path = Paths.get("whitelist.json");
        whitelist.download(path).join();
        try (FileInputStream input = new FileInputStream("whitelist.json")) {
            assertEquals(content,
                    new BufferedReader(new InputStreamReader(input)).lines().collect(Collectors.joining("\n")));
        }

        whitelist.delete().join();
        ExecutionException exception = assertThrows(ExecutionException.class, whitelist.fetch()::get);
        assertInstanceOf(APIException.class, exception.getCause());

        whitelist.putContent("[]").join();
        assertEquals("[]", whitelist.getContent().join());

        whitelist.upload(path).join();
        assertEquals(content, whitelist.getContent().join());

        Files.delete(path);
    }
}
