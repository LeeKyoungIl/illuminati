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

package me.phoboslabs.illuminati.annotation;

import me.phoboslabs.illuminati.annotation.enums.PackageType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by leekyoungil (leekyoungil@gmail.com) on 18/07/2017.
 */
@Target(value = {ElementType.METHOD, ElementType.TYPE})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Illuminati {

    boolean ignore() default false;

    int samplingRate() default 0;

    PackageType packageType() default PackageType.DEFAULT;

    String[] ignoreProfile() default {};

    String profileKeyword() default "spring.profiles.active";

    boolean isTest() default false;
}
