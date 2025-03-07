import com.exaroton.api.ExarotonClient;
import com.exaroton.api.server.Server;
import com.exaroton.api.server.ServerStatus;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.util.HashSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public abstract class APIClientTest {
    protected static final @NotNull String TEST_SERVER_ID = System.getenv("EXAROTON_TEST_SERVER");
    protected ExarotonClient client;
    protected Server server;

    @BeforeEach
    void setUp() {
        client = new ExarotonClient(System.getenv("EXAROTON_API_TOKEN"));
        server = client.getServer(TEST_SERVER_ID);

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    void tearDown() throws IOException, ExecutionException, InterruptedException, TimeoutException {
        server.fetch().join();

        if (server.hasStatus(ServerStatus.RESTARTING, ServerStatus.LOADING, ServerStatus.PREPARING)) {
            var stoppableOrStopped = new HashSet<>(ServerStatus.GROUP_OFFLINE);
            stoppableOrStopped.add(ServerStatus.STARTING);
            stoppableOrStopped.add(ServerStatus.ONLINE);

            server.waitForStatus(stoppableOrStopped)
                    .get(1, TimeUnit.MINUTES);
        }

        if (!server.hasStatus(ServerStatus.GROUP_OFFLINE)) {
            server.stop().join();
            server.waitForStatus(ServerStatus.GROUP_OFFLINE).get(1, TimeUnit.MINUTES);
        }

        server.unsubscribe();
    }
}
