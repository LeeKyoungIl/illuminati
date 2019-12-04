package me.phoboslabs.illuminati.processor.infra;

/**
 * Created by leekyoungil (leekyoungil@gmail.com) on 10/07/2017.
 */
public interface IlluminatiInfraTemplate<T> {

    void sendToIlluminati (final T entity) throws Exception;

    boolean canIConnect ();

    void connectionClose();
}
