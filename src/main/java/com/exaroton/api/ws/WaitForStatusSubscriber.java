package com.exaroton.api.ws;

import com.exaroton.api.server.Server;
import com.exaroton.api.server.ServerStatus;
import com.exaroton.api.ws.stream.ServerStatusStream;
import com.exaroton.api.ws.subscriber.ServerStatusSubscriber;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.*;

@ApiStatus.Internal
public final class WaitForStatusSubscriber implements ServerStatusSubscriber, Future<Server> {
    private final Set<ServerStatus> statuses;
    private final CompletableFuture<Server> future;
    private final ServerStatusStream stream;

    /**
     * Create a new future that will complete when the server has the specified status. This class will register itself
     * as a subscriber to the stream. Registering it externally may result in unexpected behavior.
     *
     * @param statuses statuses to wait for
     * @param stream   stream to subscribe to
     */
    public WaitForStatusSubscriber(Set<ServerStatus> statuses, ServerStatusStream stream) {
        this.statuses = Objects.requireNonNull(statuses);
        this.future = new CompletableFuture<>();
        this.stream = stream;
        Objects.requireNonNull(stream).addSubscriber(this);
    }

    @Override
    public void handleStatusUpdate(Server oldServer, Server newServer) {
        try {
            if (newServer.hasStatus(statuses)) {
                synchronized (this) {
                    future.complete(newServer);
                    stream.removeSubscriber(this);
                }
            }
        } catch (Throwable t) {
            synchronized (this) {
                future.completeExceptionally(t);
                stream.removeSubscriber(this);
            }
        }
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        synchronized (this) {
            stream.removeSubscriber(this);
            return future.cancel(mayInterruptIfRunning);
        }
    }

    @Override
    public boolean isCancelled() {
        return future.isCancelled();
    }

    @Override
    public boolean isDone() {
        return future.isDone();
    }

    @Override
    public Server get() throws InterruptedException, ExecutionException {
        try {
            return future.get();
        } finally {
            stream.removeSubscriber(this);
        }
    }

    @Override
    public Server get(long timeout, @NotNull TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        try {
            return future.get(timeout, unit);
        } finally {
            stream.removeSubscriber(this);
        }
    }
}
