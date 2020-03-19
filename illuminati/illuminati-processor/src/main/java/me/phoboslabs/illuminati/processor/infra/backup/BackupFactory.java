/*
 * Copyright 2017 Phoboslabs.me
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
