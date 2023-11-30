package com.exaroton.api.server.config;

import com.exaroton.api.APIException;
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.request.server.files.GetConfigOptionsRequest;
import com.exaroton.api.request.server.files.UpdateConfigOptionsRequest;
import com.exaroton.api.server.Server;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ServerConfig {
    protected ExarotonClient client;
    protected Server server;
    protected String path;
    protected Map<String, ConfigOption> options = null;

    public ServerConfig(ExarotonClient client, Server server, String path) {
        this.client = client;
        this.server = server;
        this.path = path;
    }

    public Map<String, ConfigOption> getOptions() throws APIException {
        return this.getOptions(false);
    }

    public Map<String, ConfigOption> getOptions(boolean update) throws APIException {
        if (this.options == null || update) {
            this.fetchOptions();
        }
        return this.options;
    }

    private void fetchOptions() throws APIException {
        setOptions(new GetConfigOptionsRequest(this.client, this.server.getId(), this.path)
                .request()
                .getData());
    }

    private void setOptions(ConfigOption[] options) {
        this.options = new HashMap<>();
        for (ConfigOption option : options) {
            this.options.put(option.getKey(), option);
        }
    }

    public @Nullable ConfigOption getOption(String key) throws APIException {
        return this.getOption(key, false);
    }

    public @Nullable ConfigOption getOption(String key, boolean update) throws APIException {
        return this.getOptions(update).get(key);
    }

    public void save() throws APIException {
        Map<String, Object> options = new HashMap<>();
        for (ConfigOption option : this.options.values()) {
            if (option.getValue() != null) {
                options.put(option.getKey(), option.getValue());
            }
        }

        setOptions(new UpdateConfigOptionsRequest(this.client, this.server.getId(), this.path, options)
                .request()
                .getData());
    }
}
