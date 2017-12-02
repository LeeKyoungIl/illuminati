package com.leekyoungil.illuminati.client.prossor.executor;

/**
 * Created by leekyoungil (leekyoungil@gmail.com) on 12/01/2017.
 */
public interface IlluminatiExecutor<T> {

    void init ();

    void addToQueue (final T t);

    T deQueue ();

    void addToQueueByDebug (final T t);

    T deQueueByDebug ();

    int getQueueSize ();

    void sendToNextStep (final T t);

    void createSystemThread ();

    // have to make the createSystemThread method in implements Class.
    //private void createSystemThread ();
}
