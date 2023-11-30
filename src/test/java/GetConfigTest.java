import com.exaroton.api.ExarotonClient;
import com.exaroton.api.server.Server;
import com.exaroton.api.server.ServerFile;
import com.exaroton.api.server.config.ConfigOption;
import com.exaroton.api.server.config.ServerConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GetConfigTest {
    private static final ExarotonClient client = new ExarotonClient(System.getenv("EXAROTON_API_TOKEN"));

    @Test
    public void testGetConfig() {
        Server server = client.getServer(System.getenv("EXAROTON_TEST_SERVER"));
        Assertions. assertDoesNotThrow(() -> {
            ServerFile serverProperties = server.getFile("server.properties");
            Assertions.assertNotNull(serverProperties);
            ServerConfig config = serverProperties.getConfig();
            Assertions.assertNotNull(config);

            ConfigOption gamemodeOption = config.getOption("gamemode");
            Assertions.assertNotNull(gamemodeOption);
            Assertions.assertEquals("Gamemode", gamemodeOption.getLabel());
            Assertions.assertNotNull(gamemodeOption.getValue());
            Assertions.assertNotNull(gamemodeOption.getOptions());
            Assertions.assertTrue(gamemodeOption.getOptions().length > 3);
        });
    }
}
