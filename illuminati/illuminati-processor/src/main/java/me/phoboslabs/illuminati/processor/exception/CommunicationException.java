package me.phoboslabs.illuminati.processor.exception;

/**
 * Created by leekyoungil (leekyoungil@gmail.com) on 10/07/2017.
 */
public class CommunicationException extends RuntimeException {

    private Throwable cause;

    public CommunicationException() {
        super();
    }

    public CommunicationException(String s) {
        super(s);
    }

    public CommunicationException(String s, Throwable throwable) {
        super(s);
        this.cause = throwable;
    }

    public CommunicationException(Throwable throwable) {
        super(throwable.toString());
        this.cause = throwable;
    }

    public Throwable getCause () {
        return this.cause;
    }
}
