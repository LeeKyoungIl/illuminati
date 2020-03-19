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

package me.phoboslabs.illuminati.common.properties;

import me.phoboslabs.illuminati.common.constant.IlluminatiConstant;
import me.phoboslabs.illuminati.common.util.StringObjectUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Properties;

public abstract class IlluminatiBaseProperties implements IlluminatiProperties, Serializable {

    protected IlluminatiBaseProperties () {}

    protected IlluminatiBaseProperties (final Properties prop) {
        this.setProperties(prop);
    }

    @Override public void setProperties(final Properties prop) {
        if (prop == null) {
            return;
        }
        for (String keys : IlluminatiConstant.PROPERTIES_KEYS) {
            final String value = prop.getProperty(keys);
            if (prop.containsKey(keys) && StringObjectUtils.isValid(value)) {
                try {
                    final Field field = this.getClass().getDeclaredField(keys);
                    field.setAccessible(true);
                    field.set(this, value);
                } catch (Exception ignored) { }
            }
        }
    }
}
