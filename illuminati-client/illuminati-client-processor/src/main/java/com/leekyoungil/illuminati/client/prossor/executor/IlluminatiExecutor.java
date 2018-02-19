package com.leekyoungil.illuminati.client.prossor.executor;

import com.leekyoungil.illuminati.common.dto.IlluminatiInterfaceModel;

/**
 * Created by leekyoungil (leekyoungil@gmail.com) on 12/01/2017.
 */
public interface IlluminatiExecutor<T extends IlluminatiInterfaceModel> {

    void init ();

    void addToQueue (final T t);

    T deQueue ();

//    void addToQueueByDebug (final T t);
//
//    T deQueueByDebug ();

    void sendToNextStep (final T t);

    int getQueueSize();

//    void createSystemThread ();

    // have to make the createSystemThread method in implements Class.
    //private void createSystemThread ();
}
