package com.exaroton.api.ws;

import com.exaroton.api.server.Server;
import com.exaroton.api.server.ServerStatus;
import com.exaroton.api.ws.stream.ServerStatusStream;
import com.exaroton.api.ws.subscriber.ServerStatusSubscriber;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.*;

@ApiStatus.Internal
public final class WaitForStatusSubscriber implements ServerStatusSubscriber, Future<Server> {
    private final Set<ServerStatus> statuses;
    private final ServerStatusStream stream;
    @Nullable
    private Server result;
    private boolean cancelled;
    @Nullable
    private Throwable throwable;
    @Nullable
    private CountDownLatch latch;

    /**
     * Create a new future that will complete when the server has the specified status. This class will register itself
     * as a subscriber to the stream. Registering it externally may result in unexpected behavior.
     * @param statuses statuses to wait for
     * @param stream stream to subscribe to
     */
    public WaitForStatusSubscriber(Set<ServerStatus> statuses, ServerStatusStream stream) {
        this.statuses = Objects.requireNonNull(statuses);
        this.stream = Objects.requireNonNull(stream);
        stream.addSubscriber(this);
    }

    @Override
    public void handleStatusUpdate(Server oldServer, Server newServer) {
        try {
            if (newServer.hasStatus(statuses)) {
                synchronized (this) {
                    this.result = newServer;
                }
            }
        } catch (Throwable t) {
            synchronized (this) {
                throwable = t;
            }
        }
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        synchronized (this) {
            if (isDone()) {
                return false;
            }

            cancelled = true;
        }
        return true;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public boolean isDone() {
        return cancelled || throwable != null || result != null;
    }

    @Override
    public Server get() throws InterruptedException, ExecutionException {
        var latch = getWaitingLatch();

        if (latch.isPresent()) {
            latch.get().await();
        }

        return getResult();
    }

    @Override
    public Server get(long timeout, @NotNull TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        var latch = getWaitingLatch();

        if (latch.isPresent()) {
            if (!latch.get().await(timeout, unit)) {
                throw new TimeoutException();
            }
        }

        stream.removeSubscriber(this);

        return getResult();
    }

    /**
     * Get the result or throw the corresponding exception. Future must have been completed before this is called.
     * @return result
     */
    private Server getResult() throws ExecutionException {
        if (cancelled) {
            throw new CancellationException();
        }

        if (throwable != null) {
            throw new ExecutionException(throwable);
        }

        return result;
    }

    /**
     * Get the latch used to wait until the future is done
     * @return latch or empty if the future is already done
     */
    private Optional<CountDownLatch> getWaitingLatch() {
        synchronized (this) {
            if (isDone()) {
                return Optional.empty();
            }

            if (latch == null) {
                latch = new CountDownLatch(1);
            }
            return Optional.of(latch);
        }
    }
}
