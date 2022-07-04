package com.exaroton.api.server;

import com.exaroton.api.APIException;
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.request.server.files.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class ServerFile {
    protected final ExarotonClient client;

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

    protected ServerFile[] children = new ServerFile[0];

    public ServerFile(ExarotonClient client, Server server, String path) {
        this.client = client;
        this.server = server;
        this.setPath(path);
    }

    /**
     * update this file with file information fetched from the API
     * @return this file object
     * @throws APIException api error
     */
    public ServerFile getInfo() throws APIException {
        GetFileInfoRequest request = new GetFileInfoRequest(this.client, this.server.getId(), this.path);
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
        return new GetFileDataRequest(this.client, this.server.getId(), this.path, "application/text")
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
        GetFileDataRequest request = new GetFileDataRequest(this.client, this.server.getId(), this.path, "octet-stream");
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
        return new GetFileDataRequest(this.client, this.server.getId(), this.path, "octet-stream")
                .requestRaw();
    }

    /**
     * write content to a text file
     * to upload a file from a path use {@link #upload(Path)}
     * to upload from an input stream use {@link #upload(InputStream)}
     * @param content file content
     */
    public void putContent(String content) throws APIException {
        new PutFileDataRequest(this.client, this.server.getId(), this.path, content)
                .request();
    }

    /**
     * upload a file
     * to write text content use {@link #putContent(String)}
     * to upload from an input stream use {@link #upload(InputStream)}
     * @param path path to file
     */
    public void upload(Path path) throws IOException, APIException {
        new PutFileDataRequest(this.client, this.server.getId(), this.path, Files.newInputStream(path))
                .request();
    }

    /**
     * upload a file
     * to write text content use {@link #putContent(String)}
     * to upload a file from a path use {@link #upload(Path)}
     * @param stream input stream
     */
    public void upload(InputStream stream) throws APIException {
        new PutFileDataRequest(this.client, this.server.getId(), this.path, stream)
                .request();
    }

    /**
     * delete this file
     * @throws APIException api error
     */
    public void delete() throws APIException {
        new DeleteFileRequest(this.client, this.server.getId(), this.path)
                .request();
    }

    /**
     * create this file as a directory
     * @throws APIException api error
     */
    public void createAsDirectory() throws APIException {
        new CreateDirectoryRequest(this.client, this.server.getId(), this.path).request();
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

    public ServerFile[] getChildren() {
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
