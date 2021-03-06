package com.exaroton.api.ws.subscriber;

import com.exaroton.api.ws.data.HeapUsage;

public abstract class HeapSubscriber extends Subscriber {

    /**
     * handle new heap data
     * @param heap heap data
     */
    public abstract void heap(HeapUsage heap);
}
