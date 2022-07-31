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

package me.phoboslabs.illuminati.processor.infra.h2;

import java.util.List;
import java.util.Map;
import me.phoboslabs.illuminati.common.dto.enums.IlluminatiInterfaceType;

public interface DBExecutor<T> {

    void appendByJsonString(IlluminatiInterfaceType illuminatiInterfaceType, String jsonStringData);

    List<T> getDataByList(boolean isPaging, boolean isAfterDelete, int from, int size) throws Exception;

    Map<Integer, T> getDataByMap(boolean isPaging, boolean isAfterDelete, int from, int size) throws Exception;

    void deleteById(int id);

    int getCount() throws Exception;
}
