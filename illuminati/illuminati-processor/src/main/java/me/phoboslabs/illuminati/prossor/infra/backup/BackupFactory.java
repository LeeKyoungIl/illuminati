package me.phoboslabs.illuminati.prossor.infra.backup;

import me.phoboslabs.illuminati.prossor.infra.backup.impl.H2Backup;
import me.phoboslabs.illuminati.common.dto.enums.IlluminatiStorageType;
import me.phoboslabs.illuminati.common.dto.impl.IlluminatiTemplateInterfaceModelImpl;

public class BackupFactory {

    public static Backup getBackupInstance (IlluminatiStorageType illuminatiStorageType) {
        switch (illuminatiStorageType) {
            case H2 :
                return H2Backup.getInstance(IlluminatiTemplateInterfaceModelImpl.class);

            default :
                break;
        }

        return null;
    }
}
