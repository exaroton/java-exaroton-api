import com.exaroton.api.ExarotonClient;
import com.exaroton.api.server.Server;
import org.jetbrains.annotations.NotNull;

public abstract class APIClientTest {
    protected static final @NotNull String TEST_SERVER_ID = System.getenv("EXAROTON_TEST_SERVER");
    protected final @NotNull ExarotonClient client = new ExarotonClient(System.getenv("EXAROTON_API_TOKEN"));
    protected final @NotNull Server server = client.getServer(TEST_SERVER_ID);
}
