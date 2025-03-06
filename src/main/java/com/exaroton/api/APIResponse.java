package com.exaroton.api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Flow;

public class APIResponse<Datatype> {

    /**
     * Request success
     */
    private final boolean success;

    /**
     * Error message
     */
    private final String error;

    /**
     * Response data
     */
    private final Datatype data;

    /**
     * Create a BodyHandler for APIResponse
     * @param gson gson instance
     * @param token type token of the response data
     * @return BodyHandler
     * @param <T> response data type
     */
    public static <T> HttpResponse.BodyHandler<APIResponse<T>> bodyHandler(ExarotonClient client, Gson gson, TypeToken<APIResponse<T>> token) {
        return responseInfo -> new BodySubscriber<>(client, gson, token);
    }

    /**
     * create an APIResponse
     *
     * @param success request success
     * @param error   error message
     * @param data    response data
     */
    public APIResponse(boolean success, String error, Datatype data) {
        this.success = success;
        this.error = error;
        this.data = data;
    }

    /**
     * @return request success
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * @return error message
     */
    public String getError() {
        return error;
    }

    /**
     * @return response data
     */
    public Datatype getData() {
        return data;
    }


    private static final class BodySubscriber<T> implements HttpResponse.BodySubscriber<APIResponse<T>> {
        private final ExarotonClient client;
        private final Gson gson;
        private final TypeToken<APIResponse<T>> token;
        private final HttpResponse.BodySubscriber<String> parent;

        private BodySubscriber(ExarotonClient client, Gson gson, TypeToken<APIResponse<T>> token) {
            this.client = client;
            this.gson = gson;
            this.token = token;
            this.parent = HttpResponse.BodySubscribers.ofString(StandardCharsets.UTF_8);
        }


        @Override
        public CompletionStage<APIResponse<T>> getBody() {
            return parent.getBody().thenCompose(json -> {
                APIResponse<T> response = gson.fromJson(json, token);
                if (!response.isSuccess()) {
                    return CompletableFuture.failedFuture(new APIException(response.getError()));
                }

                if (response.getData() instanceof Initializable) {
                    ((Initializable) response.getData()).initialize(client, gson);
                }

                return CompletableFuture.completedFuture(response);
            });
        }

        @Override
        public void onSubscribe(Flow.Subscription subscription) {
            parent.onSubscribe(subscription);
        }

        @Override
        public void onNext(List<ByteBuffer> item) {
            parent.onNext(item);
        }

        @Override
        public void onError(Throwable throwable) {
            parent.onError(throwable);
        }

        @Override
        public void onComplete() {
            parent.onComplete();
        }
    }
}
