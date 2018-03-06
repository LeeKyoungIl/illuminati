package com.leekyoungil.illuminati.client.prossor.infra.backup;

import com.leekyoungil.illuminati.client.prossor.infra.backup.impl.H2Backup;
import com.leekyoungil.illuminati.common.dto.enums.IlluminatiStorageType;
import com.leekyoungil.illuminati.common.dto.impl.IlluminatiTemplateInterfaceModelImpl;

public class BackupFactory {

    public static Backup getBackupInstance (IlluminatiStorageType illuminatiStorageType) {
        switch (illuminatiStorageType) {
            case H2 :
                return H2Backup.getInstance(IlluminatiTemplateInterfaceModelImpl.class);
        }

        return null;
    }
}
