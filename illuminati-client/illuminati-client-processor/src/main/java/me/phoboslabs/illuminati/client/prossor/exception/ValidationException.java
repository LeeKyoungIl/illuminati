package me.phoboslabs.illuminati.client.prossor.exception;

/**
 * Created by leekyoungil (leekyoungil@gmail.com) on 10/07/2017.
 */
public class ValidationException extends RuntimeException {

    private Throwable cause;

    public ValidationException () {
        super();
    }

    public ValidationException (String s) {
        super(s);
    }

    public ValidationException (String s, Throwable throwable) {
        super(s);
        this.cause = throwable;
    }

    public ValidationException (Throwable throwable) {
        super(throwable);
    }

    public Throwable getCause () {
        return this.cause;
    }
}
