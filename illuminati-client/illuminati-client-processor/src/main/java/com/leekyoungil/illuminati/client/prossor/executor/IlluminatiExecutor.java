package com.leekyoungil.illuminati.client.prossor.executor;

public interface IlluminatiExecutor<T> {

    void init ();

    void addToQueue (final T t);

    T deQueue ();

    int getQueueSize ();

    void sendToNextStep (final T t);

    void createSystemThread ();

    // have to make the createSystemThread method in implements Class.
    //private void createSystemThread ();
}
