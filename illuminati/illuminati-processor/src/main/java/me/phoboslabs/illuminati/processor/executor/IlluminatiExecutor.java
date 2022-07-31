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

package me.phoboslabs.illuminati.processor.executor;

import me.phoboslabs.illuminati.common.dto.IlluminatiInterfaceModel;
import me.phoboslabs.illuminati.processor.exception.RequiredValueException;

/**
 * Created by leekyoungil (leekyoungil@gmail.com) on 12/01/2017.
 */
public interface IlluminatiExecutor<T extends IlluminatiInterfaceModel> {

    IlluminatiExecutor init() throws RequiredValueException;

    void addToQueue(T t);

    T deQueue() throws Exception;

    void sendToNextStep(T t) throws Exception;

    int getQueueSize();
}
