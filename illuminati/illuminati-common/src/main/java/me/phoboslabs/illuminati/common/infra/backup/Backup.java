package me.phoboslabs.illuminati.common.infra.backup;

import me.phoboslabs.illuminati.common.dto.IlluminatiModel;

public interface Backup {

    void addToBackupQueue(IlluminatiModel illuminatiModel);
}
