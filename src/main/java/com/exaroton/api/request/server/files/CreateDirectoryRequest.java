package com.exaroton.api.request.server.files;

import com.exaroton.api.ExarotonClient;

import java.io.InputStream;
import java.util.HashMap;

public class CreateDirectoryRequest extends FileDataRequest {
    public CreateDirectoryRequest(ExarotonClient client, String serverId, String path) {
        super(client, serverId, path);
    }

    @Override
    protected String getMethod() {
        return "PUT";
    }

    @Override
    protected HashMap<String, String> getHeaders() {
        HashMap<String, String> map = super.getHeaders();
        map.put("Content-Type", "inode/directory");
        return map;
    }
}
