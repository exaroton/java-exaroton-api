package com.exaroton.api.server;

import com.exaroton.api.APIException;
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.request.server.files.*;
import com.exaroton.api.server.config.ServerConfig;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class ServerFile {
    protected transient final ExarotonClient client;

    protected transient final Gson gson;

    protected final Server server;

    protected String path;

    protected String name;

    protected boolean isTextFile;

    protected boolean isConfigFile;

    protected boolean isDirectory;

    protected boolean isLog;

    protected boolean isReadable;

    protected boolean isWritable;

    protected int size;

    protected Collection<ServerFile> children = List.of();

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
     * @throws APIException api error
     */
    public ServerFile getInfo() throws APIException {
        GetFileInfoRequest request = new GetFileInfoRequest(this.client, this.gson, this.server.getId(), this.path);
        return this.setFromObject(request.request().getData());
    }

    /**
     * get the contents of this text file
     * to read a different file use {@link #downloadStream()}
     * to download a file use {@link #download(Path)}
     * @return file content
     * @throws APIException api error
     */
    public String getContent() throws APIException {
        return new GetFileDataRequest(this.client, this.gson, this.server.getId(), this.path, "application/text")
                .requestString();
    }

    /**
     * save this file to a path
     * to read a text different file use {@link #getContent()}
     * to read a different file use {@link #downloadStream()}
     * @param path output file path
     * @throws APIException api error
     * @throws IOException failed to write file
     */
    public void download(Path path) throws APIException, IOException {
        GetFileDataRequest request = new GetFileDataRequest(this.client, this.gson, this.server.getId(), this.path, "octet-stream");
        try (InputStream stream = request.requestRaw()) {
            Files.copy(stream, path, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * get the download stream of this file
     * to read a text different file use {@link #getContent()}
     * to download a file use {@link #download(Path)}
     * @throws APIException API error
     * @return input stream for file data
     */
    public InputStream downloadStream() throws APIException {
        return new GetFileDataRequest(this.client, this.gson, this.server.getId(), this.path, "octet-stream")
                .requestRaw();
    }

    /**
     * write content to a text file
     * to upload a file from a path use {@link #upload(Path)}
     * to upload from an input stream use {@link #upload(InputStream)}
     * @param content file content
     */
    public void putContent(String content) throws APIException {
        new PutFileDataRequest(this.client, this.gson, this.server.getId(), this.path, content)
                .request();
    }

    /**
     * upload a file
     * to write text content use {@link #putContent(String)}
     * to upload from an input stream use {@link #upload(InputStream)}
     * @param path path to file
     */
    public void upload(Path path) throws IOException, APIException {
        new PutFileDataRequest(this.client, this.gson, this.server.getId(), this.path, Files.newInputStream(path))
                .request();
    }

    /**
     * upload a file
     * to write text content use {@link #putContent(String)}
     * to upload a file from a path use {@link #upload(Path)}
     * @param stream input stream
     */
    public void upload(InputStream stream) throws APIException {
        new PutFileDataRequest(this.client, this.gson, this.server.getId(), this.path, stream)
                .request();
    }

    /**
     * delete this file
     * @throws APIException api error
     */
    public void delete() throws APIException {
        new DeleteFileRequest(this.client, this.gson, this.server.getId(), this.path)
                .request();
    }

    /**
     * create this file as a directory
     * @throws APIException api error
     */
    public void createAsDirectory() throws APIException {
        new CreateDirectoryRequest(this.client, this.gson, this.server.getId(), this.path).request();
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
    public ServerFile setFromObject(ServerFile file) {
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
