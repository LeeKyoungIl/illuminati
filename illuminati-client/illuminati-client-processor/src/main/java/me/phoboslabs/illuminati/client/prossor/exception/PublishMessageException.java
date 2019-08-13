package me.phoboslabs.illuminati.client.prossor.exception;

/**
 * Created by leekyoungil (leekyoungil@gmail.com) on 10/07/2017.
 */
public class PublishMessageException extends RuntimeException {

    private Throwable cause;

    public PublishMessageException() {
        super();
    }

    public PublishMessageException(String s) {
        super(s);
    }

    public PublishMessageException(String s, Throwable throwable) {
        super(s);
        this.cause = throwable;
    }

    public PublishMessageException(Throwable throwable) {
        super(throwable.toString());
        this.cause = throwable;
    }

    public Throwable getCause () {
        return this.cause;
    }
}
