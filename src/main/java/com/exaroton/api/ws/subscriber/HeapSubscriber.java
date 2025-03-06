package com.exaroton.api.ws.subscriber;

import com.exaroton.api.ws.data.HeapUsage;

public interface HeapSubscriber {

    /**
     * handle new heap data
     * @param heap heap data
     */
    void handleHeapUsage(HeapUsage heap);
}
