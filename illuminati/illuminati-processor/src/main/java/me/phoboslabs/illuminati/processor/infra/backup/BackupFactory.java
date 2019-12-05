package me.phoboslabs.illuminati.processor.infra.backup;

import me.phoboslabs.illuminati.processor.infra.backup.impl.H2Backup;
import me.phoboslabs.illuminati.common.dto.enums.IlluminatiStorageType;
import me.phoboslabs.illuminati.common.dto.impl.IlluminatiTemplateInterfaceModelImpl;

public class BackupFactory {

    public static Backup getBackupInstance (IlluminatiStorageType illuminatiStorageType) throws Exception {
        switch (illuminatiStorageType) {
            case H2 :
                return H2Backup.getInstance(IlluminatiTemplateInterfaceModelImpl.class);

            default :
                throw new Exception("Backup function is only supported by the H2 Database yet.");
        }
    }
}
