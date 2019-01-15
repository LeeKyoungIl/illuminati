package me.phoboslabs.illuminati.client.prossor.infra.enums;

/**
 * Created by leekyoungil (leekyoungil@gmail.com) on 12/07/2017.
 */
public enum DurabillityType {

    DURABILE(0),
    TRANSIENT(1);

    private final int type;

    DurabillityType (int type) {
        this.type = type;
    }

    public int getType () {
        return this.type;
    }
}
