package me.phoboslabs.illuminati.processor.executor;

import me.phoboslabs.illuminati.common.dto.IlluminatiInterfaceModel;

/**
 * Created by leekyoungil (leekyoungil@gmail.com) on 12/01/2017.
 */
public interface IlluminatiExecutor<T extends IlluminatiInterfaceModel> {

    void init ();

    void addToQueue (final T t);

    T deQueue () throws Exception;

    void sendToNextStep (final T t) throws Exception;

    int getQueueSize();
}
