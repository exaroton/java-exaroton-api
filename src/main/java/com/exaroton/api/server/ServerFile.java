package com.exaroton.api.server;

import com.exaroton.api.APIException;
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
     * update this file with file information fetched from the API
     * @return this file object
     * @throws IOException connection errors
     */
    public CompletableFuture<ServerFile> get() throws IOException {
        return client.request(new GetFileInfoRequest(this.client, this.gson, this.server.getId(), this.path))
                .thenApply(this::setFromObject);
    }

    /**
     * get the contents of this text file
     * to read a different file use {@link #downloadStream()}
     * to download a file use {@link #download(Path)}
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
     * save this file to a path
     * to read a text different file use {@link #getContent()}
     * to read a different file use {@link #downloadStream()}
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
     * get the download stream of this file
     * to read a text different file use {@link #getContent()}
     * to download a file use {@link #download(Path)}
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
     * write content to a text file
     * to upload a file from a path use {@link #upload(Path)}
     * to upload from an input stream use {@link #upload(InputStream)}
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
     * upload a file
     * to write text content use {@link #putContent(String)}
     * to upload from an input stream use {@link #upload(InputStream)}
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
     * upload a file
     * to write text content use {@link #putContent(String)}
     * to upload a file from a path use {@link #upload(Path)}
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
     * upload a file
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
     * delete this file
     *
     * @return future for deletion completion
     * @throws IOException connection errors
     */
    public CompletableFuture<Void> delete() throws IOException {
        return client.request(new DeleteFileRequest(this.client, this.gson, this.server.getId(), this.path));
    }

    /**
     * create this file as a directory
     *
     * @return future for creation completion
     * @throws IOException connection errors
     */
    public CompletableFuture<Void> createAsDirectory() throws IOException {
        return client.request(new CreateDirectoryRequest(this.client, this.gson, this.server.getId(), this.path));
    }

    /**
     * get a ServerConfig object for this file
     * @return server config object
     */
    public ServerConfig getConfig() {
        return new ServerConfig(this.client, this.gson, this.server, this.path);
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public boolean isTextFile() {
        return isTextFile;
    }

    public boolean isConfigFile() {
        return isConfigFile;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public boolean isLog() {
        return isLog;
    }

    public boolean isReadable() {
        return isReadable;
    }

    public boolean isWritable() {
        return isWritable;
    }

    public int getSize() {
        return size;
    }

    public Collection<ServerFile> getChildren() {
        return children;
    }

    /**
     * set file path
     * @param path new path
     */
    public void setPath(String path) {
        this.path = path.replaceAll("^/+", "");
    }

    /**
     * update properties from fetched object
     * @param file file fetched from the API
     * @return updated file object
     */
    private ServerFile setFromObject(ServerFile file) {
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
