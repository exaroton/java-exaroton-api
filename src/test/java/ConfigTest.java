import com.exaroton.api.APIException;
import com.exaroton.api.server.ServerFile;
import com.exaroton.api.server.config.ConfigOption;
import com.exaroton.api.server.config.ServerConfig;
import com.exaroton.api.server.config.options.SelectConfigOption;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class ConfigTest extends APIClientTest {
    @Test
    public void testGetConfig() throws IOException {
        ServerFile serverProperties = server.getFile("server.properties");
        Assertions.assertNotNull(serverProperties);
        ServerConfig config = serverProperties.getConfig();
        Assertions.assertNotNull(config);

        ConfigOption<?> gamemodeOption = config.getOption("gamemode").join();
        Assertions.assertInstanceOf(SelectConfigOption.class, gamemodeOption);
        SelectConfigOption select = (SelectConfigOption) gamemodeOption;

        Assertions.assertNotNull(select);
        Assertions.assertEquals("Gamemode", select.getLabel());
        Assertions.assertNotNull(select.getValue());

        Assertions.assertNotNull(select.getOptions());
        Assertions.assertTrue(select.getOptions().size() > 3);
    }
}
