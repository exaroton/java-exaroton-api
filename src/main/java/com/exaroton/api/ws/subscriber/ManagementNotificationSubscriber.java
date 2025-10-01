package com.exaroton.api.ws.subscriber;

import com.google.gson.JsonElement;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.AvailableSince("2.4.0")
public interface ManagementNotificationSubscriber {
    /**
     * Handle a notification from the managed server
     * @param name name of the notification
     * @param data data of the notification (can be null)
     */
    void handleNotification(@NotNull String name, @Nullable JsonElement data);
}
