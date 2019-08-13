package me.phoboslabs.illuminati.client.prossor.exception;

import javax.lang.model.element.Element;

/**
 * Created by leekyoungil (leekyoungil@gmail.com) on 20/07/2017.
 */
public class ProcessingException extends Exception {

    private Element element;

    public ProcessingException(Element element, String msg, Object... args) {
        super(String.format(msg, args));
        this.element = element;
    }

    public Element getElement() {
        return element;
    }
}
