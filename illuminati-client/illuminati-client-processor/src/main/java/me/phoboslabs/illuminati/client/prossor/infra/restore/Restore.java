package me.phoboslabs.illuminati.client.prossor.infra.restore;

public interface Restore {

    void init ();

    void restoreToQueue ();

    void restoreToQueueByDebug ();
}
