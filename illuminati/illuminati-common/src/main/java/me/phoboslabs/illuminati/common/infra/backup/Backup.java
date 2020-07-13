package me.phoboslabs.illuminati.common.infra.backup;

import me.phoboslabs.illuminati.common.dto.IlluminatiInterfaceModel;

public interface Backup {

    void addToBackupQueue(IlluminatiInterfaceModel illuminatiInterfaceModel);
}
