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

package me.phoboslabs.illuminati.backup.infra.backup.shutdown;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * - @marcus.moon provided me with an Graceful idea.
 * 
 * Created by leekyoungil (leekyoungil@gmail.com) on 04/05/2018.
 */
public class IlluminatiGracefulShutdownChecker {

    private final static AtomicBoolean ILLUMINATI_READY_TO_SHUTDOWN = new AtomicBoolean(false);

    public IlluminatiGracefulShutdownChecker() {}

    public static boolean getIlluminatiReadyToShutdown() {
        return ILLUMINATI_READY_TO_SHUTDOWN.get();
    }

    public static void setIlluminatiReadyToShutdown (boolean readyToShutdown) {
        ILLUMINATI_READY_TO_SHUTDOWN.lazySet(readyToShutdown);
    }
}
