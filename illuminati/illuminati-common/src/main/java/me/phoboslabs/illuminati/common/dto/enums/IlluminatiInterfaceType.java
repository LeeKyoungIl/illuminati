package me.phoboslabs.illuminati.common.dto.enums;

public enum IlluminatiInterfaceType {

    DATA_EXECUTOR(1),
    TEMPLATE_EXECUTOR(2);

    private final int executorTypeId;

    IlluminatiInterfaceType(int executorTypeId) {
        this.executorTypeId = executorTypeId;
    }

    public int getExecutorId () {
        return this.executorTypeId;
    }
}
