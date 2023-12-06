import com.exaroton.api.ExarotonClient;
import com.exaroton.api.server.Server;

public abstract class APIClientTest {
    protected static final String TEST_SERVER_ID = System.getenv("EXAROTON_TEST_SERVER");
    protected final ExarotonClient client = new ExarotonClient(System.getenv("EXAROTON_API_TOKEN"));
    protected final Server server = client.getServer(TEST_SERVER_ID);
}
