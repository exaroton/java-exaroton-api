import com.exaroton.api.APIException;
import com.exaroton.api.server.Server;
import com.exaroton.api.server.ServerFile;
import com.exaroton.api.server.config.ConfigOption;
import com.exaroton.api.server.config.ServerConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ConfigTest extends APIClientTest {
    @Test
    public void testGetConfig() throws APIException {
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
    }
}
