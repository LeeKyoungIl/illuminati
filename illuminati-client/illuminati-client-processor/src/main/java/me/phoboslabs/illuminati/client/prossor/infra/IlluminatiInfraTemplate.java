package me.phoboslabs.illuminati.client.prossor.infra;

/**
 * Created by leekyoungil (leekyoungil@gmail.com) on 10/07/2017.
 */
public interface IlluminatiInfraTemplate<T> {

    void sendToIlluminati (final T entity);

    boolean canIConnect ();

    void connectionClose();
}
