package com.exaroton.api.server;

import com.exaroton.api.ExarotonClient;
import com.exaroton.api.request.server.files.*;
import com.exaroton.api.server.config.ServerConfig;
import com.google.gson.Gson;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public final class ServerFile {
    private boolean fetched;

    private transient final ExarotonClient client;

    private transient final Gson gson;

    private final Server server;

    private String path;

    private String name;

    private boolean isTextFile;

    private boolean isConfigFile;

    private boolean isDirectory;

    private boolean isLog;

    private boolean isReadable;

    private boolean isWritable;

    private int size;

    private Collection<ServerFile> children = List.of();

    @ApiStatus.Internal
    public ServerFile(
            @NotNull ExarotonClient client,
            @NotNull Gson gson,
            @NotNull Server server,
            @NotNull String path
    ) {
        this.client = Objects.requireNonNull(client);
        this.gson = Objects.requireNonNull(gson);
        this.server = Objects.requireNonNull(server);
        this.setPath(Objects.requireNonNull(path));
    }

    /**
     * Get the path of this file on the server.
     * @return file path
     */
    public String getPath() {
        return path;
    }

    /**
     * Has this file been fetched from the API yet. If this is false only the path is known.
     * @return has this file been fetched from the API yet
     */
    public boolean isFetched() {
        return fetched;
    }

    /**
     * Get the file name of this file.
     * @return file name
     */
    public String getName() {
        return name;
    }

    /**
     * Is this file a text file.
     * @return is this file a text file
     */
    public boolean isTextFile() {
        return isTextFile;
    }

    /**
     * Is this file a config file. Config files can be parsed and updated with the {@link ServerConfig} class.
     * @return is this file a config file
     * @see #getConfig()
     */
    public boolean isConfigFile() {
        return isConfigFile;
    }

    /**
     * Is this file a directory.
     * @return is this file a directory
     */
    public boolean isDirectory() {
        return isDirectory;
    }

    /**
     * Is this file a log file.
     * @return is this file a log file
     */
    public boolean isLog() {
        return isLog;
    }

    /**
     * Is this file readable.
     * @return is this file readable
     */
    public boolean isReadable() {
        return isReadable;
    }

    /**
     * Is this file writable.
     * @return is this file writable
     */
    public boolean isWritable() {
        return isWritable;
    }

    /**
     * Get the size of this file in bytes.
     * @return file size
     */
    public int getSize() {
        return size;
    }

    /**
     * Get the children of this directory.
     * @return children of this directory
     */
    public Collection<ServerFile> getChildren() {
        return children;
    }

    /**
     * get a ServerConfig object for this file
     * @return server config object
     */
    public ServerConfig getConfig() {
        return new ServerConfig(this.client, this.gson, this.server, this.path);
    }

    /**
     * Fetch this file from the API
     *
     * @param force force fetching the file even if it has already been fetched
     * @return this file object
     * @throws IOException connection errors
     */
    public CompletableFuture<ServerFile> fetch(boolean force) throws IOException {
        if (!force && isFetched()) {
            return CompletableFuture.completedFuture(this);
        }

        return client.request(new GetFileInfoRequest(this.client, this.gson, this.server.getId(), this.path))
                .thenApply(this::setFromObject);
    }

    public CompletableFuture<ServerFile> fetch() throws IOException {
        return fetch(true);
    }

    /**
     * Get the contents of this text file. For other file types use {@link #downloadStream()}. To download a file use
     * {@link #download(Path)}.
     *
     * @return file content
     * @throws IOException connection errors
     */
    public CompletableFuture<String> getContent() throws IOException {
        var request = new GetFileDataRequest(
                this.client,
                this.gson,
                this.server.getId(),
                this.path,
                "application/text"
        );
        return client.request(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Download this file to a path. To read a text file use {@link #getContent()}. For other file types use
     * {@link #downloadStream()}
     *
     * @param path output file path
     * @return future for download completion
     * @throws IOException connection errors
     */
    public CompletableFuture<Void> download(Path path) throws IOException {
        return downloadStream().thenAccept(response -> {
            try (InputStream stream = response) {
                Files.copy(stream, path, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Get the download stream of this file. For reading a text file use {@link #getContent()}. To download a file use
     * {@link #download(Path)}
     *
     * @throws IOException connection errors
     * @return input stream for file data
     */
    public CompletableFuture<InputStream> downloadStream() throws IOException {
        var request = new GetFileDataRequest(
                this.client,
                this.gson,
                this.server.getId(),
                this.path,
                "octet-stream"
        );

        return client.request(request, HttpResponse.BodyHandlers.ofInputStream());
    }

    /**
     * Write content to a text file. To upload a file from a path use {@link #upload(Path)}. To upload from an input
     * stream use {@link #upload(InputStream)}.
     *
     * @param content file content
     * @return future for upload completion
     * @throws IOException if the API returns an error
     * @see #upload(Path)
     * @see #upload(InputStream)
     * @see #upload(Supplier)
     */
    public CompletableFuture<Void> putContent(String content) throws IOException {
        return client.request(new PutFileDataRequest(this.client, this.gson, this.server.getId(), this.path, content));
    }

    /**
     * Upload a local file. To write text content use {@link #putContent(String)}. To upload from an input stream use
     * {@link #upload(InputStream)}.
     *
     * @param path path to file
     * @return future for upload completion
     * @throws IOException if the API returns an error
     * @see #putContent(String)
     * @see #upload(InputStream)
     * @see #upload(Supplier)
     */
    public CompletableFuture<Void> upload(Path path) throws IOException {
        return upload(Files.newInputStream(path));
    }

    /**
     * Write an input stream to this file. To write text content use {@link #putContent(String)}. To upload a local file
     * from a path use {@link #upload(Path)}
     *
     * @param stream input stream
     * @return future for upload completion
     * @throws IOException if the API returns an error
     * @see #putContent(String)
     * @see #upload(Path)
     * @see #upload(Supplier)
     */
    public CompletableFuture<Void> upload(InputStream stream) throws IOException {
        return upload(() -> stream);
    }

    /**
     * Write an input stream to this file. To write text content use {@link #putContent(String)}. To upload a local file
     * from a path use {@link #upload(Path)}
     *
     * @param stream input stream supplier
     * @return future for upload completion
     * @throws IOException if the API returns an error
     * @see #putContent(String)
     * @see #upload(Path)
     * @see #upload(InputStream)
     */
    public CompletableFuture<Void> upload(Supplier<InputStream> stream) throws IOException {
        return client.request(new PutFileDataRequest(this.client, this.gson, this.server.getId(), this.path, stream));
    }

    /**
     * Delete this file
     *
     * @return future for deletion completion
     * @throws IOException connection errors
     */
    public CompletableFuture<Void> delete() throws IOException {
        return client.request(new DeleteFileRequest(this.client, this.gson, this.server.getId(), this.path));
    }

    /**
     * Create this file as a directory
     *
     * @return future for creation completion
     * @throws IOException connection errors
     */
    public CompletableFuture<Void> createAsDirectory() throws IOException {
        return client.request(new CreateDirectoryRequest(this.client, this.gson, this.server.getId(), this.path));
    }

    /**
     * Set file path
     * @param path new path
     */
    private void setPath(String path) {
        this.path = path.replaceAll("^/+", "");
    }

    /**
     * Update properties from fetched object
     * @param file file fetched from the API
     * @return updated file object
     */
    private ServerFile setFromObject(ServerFile file) {
        fetched = true;
        if (file.path != null) {
            this.setPath(path);
        }
        this.name = file.name;
        this.isTextFile = file.isTextFile;
        this.isConfigFile = file.isConfigFile;
        this.isDirectory = file.isDirectory;
        this.isLog = file.isLog;
        this.isReadable = file.isReadable;
        this.isWritable = file.isWritable;
        this.size = file.size;
        this.children = file.children;
        return this;
    }
}
