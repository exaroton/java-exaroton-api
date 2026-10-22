package com.exaroton.api.request.software;

import com.exaroton.api.APIRequest;
import com.exaroton.api.APIResponse;
import com.exaroton.api.software.Software;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.Nullable;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class GetSoftwareListRequest extends APIRequest<List<Software>> {
    private final @Nullable String type;
    private final @Nullable String launcher;

    public GetSoftwareListRequest(@Nullable String type, @Nullable String launcher) {
        super();
        this.type = type;
        this.launcher = launcher;
    }

    @Override
    protected String getEndpoint() {
        return "software/";
    }

    @Override
    protected String getPath() {
        List<String> query = new ArrayList<>();
        if (type != null) {
            query.add("type=" + URLEncoder.encode(type, StandardCharsets.UTF_8));
        }
        if (launcher != null) {
            query.add("launcher=" + URLEncoder.encode(launcher, StandardCharsets.UTF_8));
        }

        String path = super.getPath();
        return query.isEmpty() ? path : path + "?" + String.join("&", query);
    }

    @Override
    protected TypeToken<APIResponse<List<Software>>> getType() {
        return new TypeToken<APIResponse<List<Software>>>(){};
    }
}
