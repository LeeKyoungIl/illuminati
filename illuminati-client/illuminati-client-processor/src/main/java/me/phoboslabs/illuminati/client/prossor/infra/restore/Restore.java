package me.phoboslabs.illuminati.client.prossor.infra.restore;

public interface Restore {

    Restore init ();

    void restoreToQueue ();

    void restoreToQueueByDebug ();
}
