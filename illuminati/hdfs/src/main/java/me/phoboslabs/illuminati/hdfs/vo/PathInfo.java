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

package me.phoboslabs.illuminati.hdfs.vo;

import org.apache.hadoop.fs.Path;

public class PathInfo {

    private final Path path;
    private final boolean isExists;

    public PathInfo(Path path, boolean isExists) {
        this.path = path;
        this.isExists = isExists;
    }

    public boolean isExists() {
        return this.isExists;
    }

    public boolean isNotExists() {
        return !this.isExists;
    }

    public Path getPath() {
        return this.path;
    }
}
