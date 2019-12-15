package me.phoboslabs.illuminati.processor.infra;

import me.phoboslabs.illuminati.processor.exception.PublishMessageException;

/**
 * Created by leekyoungil (leekyoungil@gmail.com) on 10/07/2017.
 */
public interface IlluminatiInfraTemplate<T> {

    void sendToIlluminati (final T entity) throws PublishMessageException, Exception;

    boolean canIConnect ();

    void connectionClose();
}
