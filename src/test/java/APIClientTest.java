import com.exaroton.api.ExarotonClient;

public abstract class APIClientTest {
    protected static final String TEST_SERVER_ID = System.getenv("EXAROTON_TEST_SERVER");
    protected final ExarotonClient client = new ExarotonClient(System.getenv("EXAROTON_API_TOKEN"));
}
