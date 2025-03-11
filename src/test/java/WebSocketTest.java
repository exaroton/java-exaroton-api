import com.exaroton.api.server.Server;
import com.exaroton.api.server.ServerStatus;
import com.exaroton.api.ws.data.HeapUsage;
import com.exaroton.api.ws.data.StatsData;
import com.exaroton.api.ws.data.TickData;
import com.exaroton.api.ws.subscriber.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

public class WebSocketTest extends APIClientTest {

    @Test
    void testRemoveSubscribersWithoutWebsocket() {
        server.removeStatusSubscriber((oldServer, newServer) -> {
        });
        server.removeConsoleSubscriber(x -> {
        });
        server.removeHeapSubscriber(x -> {
        });
        server.removeStatsSubscriber(x -> {
        });
        server.removeTickSubscriber(x -> {
        });
    }

    /**
     * Perform a series of tests that require the server to be online
     */
    @Test
    void testStartServer() throws IOException, ExecutionException, InterruptedException, TimeoutException {
        server.fetch().join();

        startServer();
        testHeapSubscriber();
        testStatsSubscriber();
        testTickSubscriber();
        testExecuteCommand();
        restartServer();
        stopServer();

        assertTrue(server.getWebSocket().isEmpty(), "Expected websocket to be closed");
    }

    void testHeapSubscriber() throws ExecutionException, InterruptedException, TimeoutException {
        CompletableFuture<HeapUsage> heapFuture = new CompletableFuture<>();
        HeapSubscriber heapSubscriber = heapFuture::complete;

        server.addHeapSubscriber(heapSubscriber);
        HeapUsage heapUsage = heapFuture.get(1, TimeUnit.MINUTES);
        server.removeHeapSubscriber(heapSubscriber);

        assertNotNull(heapUsage);
        assertTrue(heapUsage.getUsage() > 0, "Expected heap usage to be greater than 0");
    }

    void testStatsSubscriber() throws ExecutionException, InterruptedException, TimeoutException {
        CompletableFuture<StatsData> statsFuture = new CompletableFuture<>();
        StatsSubscriber statsSubscriber = statsFuture::complete;

        server.addStatsSubscriber(statsSubscriber);
        StatsData stats = statsFuture.get(1, TimeUnit.MINUTES);
        server.removeStatsSubscriber(statsSubscriber);

        assertNotNull(stats);
        assertTrue(stats.getMemory().getUsage() > 0, "Expected memory usage to be greater than 0");
        assertTrue(stats.getMemory().getPercent() > 0, "Expected memory usage to be greater than 0%");
        assertTrue(stats.getMemory().getPercent() < 100, "Expected memory usage to be less than 100%");
    }

    void testTickSubscriber() throws ExecutionException, InterruptedException, TimeoutException {
        CompletableFuture<TickData> tickFuture = new CompletableFuture<>();
        TickSubscriber tickSubscriber = tickFuture::complete;

        server.addTickSubscriber(tickSubscriber);
        TickData tick = tickFuture.get(1, TimeUnit.MINUTES);
        server.removeTickSubscriber(tickSubscriber);

        assertNotNull(tick);
        assertTrue(tick.getAverageTickTime() > 0, "Expected tick time to be greater than 0");
        assertTrue(tick.calculateTPS() > 0, "Expected tps to be greater than 0");
        assertTrue(tick.calculateTPS() <= 20, "Expected memory usage to be greater than 0%");
    }

    void testExecuteCommand() throws IOException, ExecutionException, InterruptedException, TimeoutException {
        var flagA = new FlagSubscriber();
        var flagB = new FlagSubscriber();

        server.addConsoleSubscriber(flagA);
        server.addConsoleSubscriber(flagB);

        server.executeCommand("say " + flagA.value).join();

        Server serverWithoutWS = client.getServer(TEST_SERVER_ID);
        serverWithoutWS.executeCommand("say " + flagB.value).join();

        flagA.future.get(10, TimeUnit.SECONDS);
        flagB.future.get(10, TimeUnit.SECONDS);

        server.removeConsoleSubscriber(flagA);
        server.removeConsoleSubscriber(flagB);
    }

    void startServer() throws IOException, ExecutionException, InterruptedException, TimeoutException {
        assertTrue(server.hasStatus(ServerStatus.GROUP_OFFLINE));

        AtomicBoolean receivedStatusUpdate = new AtomicBoolean(false);
        var statusSubscriber = new ServerStatusSubscriber() {
            @Override
            public void handleStatusUpdate(Server oldServer, Server newServer) {
                receivedStatusUpdate.set(true);
                assertSame(server, newServer);
            }
        };
        server.addStatusSubscriber(statusSubscriber);

        server.start().join();
        server.waitForStatus(ServerStatus.ONLINE).get(3, TimeUnit.MINUTES);
        assertEquals(ServerStatus.ONLINE, server.getStatus());

        server.removeStatusSubscriber(statusSubscriber);
        assertTrue(receivedStatusUpdate.get());
    }

    void restartServer() throws IOException, ExecutionException, InterruptedException, TimeoutException {
        assertEquals(ServerStatus.ONLINE, server.getStatus());

        var restartingFuture = server.waitForStatus(ServerStatus.RESTARTING);
        assertTrue(server.getWebSocket().isPresent());
        server.getWebSocket().get().waitForReady().get(1, TimeUnit.MINUTES);

        server.restart().join();
        restartingFuture.get(1, TimeUnit.MINUTES);
        assertEquals(ServerStatus.RESTARTING, server.getStatus());


        server.waitForStatus(ServerStatus.ONLINE).get(3, TimeUnit.MINUTES);
        assertEquals(ServerStatus.ONLINE, server.getStatus());
    }

    void stopServer() throws IOException, ExecutionException, InterruptedException, TimeoutException {
        assertFalse(server.hasStatus(ServerStatus.GROUP_OFFLINE));
        assertFalse(server.hasStatus(ServerStatus.GROUP_STOPPING));

        server.stop().join();
        server.waitForStatus(ServerStatus.GROUP_OFFLINE).get(3, TimeUnit.MINUTES);
        assertTrue(server.hasStatus(ServerStatus.GROUP_OFFLINE), "Expected server to be offline or crashed");
    }

    private static class FlagSubscriber implements ConsoleSubscriber {
        final String value;
        final CompletableFuture<Void> future = new CompletableFuture<>();

        public FlagSubscriber() {
            this.value = "exaroton-api-tests-" + new Random().nextInt();
        }

        @Override
        public void handleLine(String message) {
            if (message.contains(value)) {
                future.complete(null);
            }
        }
    }
}
