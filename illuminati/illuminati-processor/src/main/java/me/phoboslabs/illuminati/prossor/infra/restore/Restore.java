package me.phoboslabs.illuminati.prossor.infra.restore;

public interface Restore {

    void init ();

    void restoreToQueue ();

    void restoreToQueueByDebug ();
}
