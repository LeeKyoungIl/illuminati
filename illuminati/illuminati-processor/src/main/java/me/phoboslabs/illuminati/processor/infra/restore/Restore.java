package me.phoboslabs.illuminati.processor.infra.restore;

public interface Restore {

    void init ();

    void restoreToQueue ();

    void restoreToQueueByDebug ();
}
