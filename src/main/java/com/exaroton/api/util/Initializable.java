package com.exaroton.api.util;

import com.exaroton.api.ExarotonClient;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;

/**
 * Interface for objects that can be initialized
 */
public interface Initializable {
    /**
     * Initialize the object
     *
     * @param client exaroton client
     * @param gson   gson instance
     */
    void initialize(@NotNull ExarotonClient client, @NotNull Gson gson);
}
