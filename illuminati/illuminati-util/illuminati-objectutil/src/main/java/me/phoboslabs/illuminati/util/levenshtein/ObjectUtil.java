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

package me.phoboslabs.illuminati.util.levenshtein;

import java.lang.reflect.Field;
import java.util.Arrays;

public class ObjectUtil {

    /**
     * <p>Compare two objects for equality. Even if the object to be compared is null,
     * it safely returns false and compares nulls.</p>
     *
     * <pre>
     * class Baz {
     *     String testBazString = "test";
     * }
     *
     * class Bar {
     *     String testBarString = "test";
     *     Baz baz = new Baz();
     *     Baz nullBaz;
     * }
     *
     * class Foo {
     *     Bar bar = new Bar();
     *     Bar nullBar;
     *     int primitiveInt = 1;
     *     String testFooString = "test";
     *
     *     public static final String PUBLIC_STATIC_FINAL_FOO = "foo";
     *     public static String PUBLIC_STATIC_NULL_FOO;
     * }
     *
     * Foo foo = new Foo();
     *
     * ObjectUtils.nullSafeEquals(foo, "nullBar", null)                          = true
     * ObjectUtils.nullSafeEquals(foo, "nullBar.testBarString", "test")          = false
     * ObjectUtils.nullSafeEquals(foo, "bar.testBarString", "test")              = true
     * ObjectUtils.nullSafeEquals(foo, "bar.nullBaz.testBazString", "test")      = false
     * ObjectUtils.nullSafeEquals(foo, "primitiveInt", 1)                        = true
     * ObjectUtils.nullSafeEquals(foo, "primitiveInt", 2)                        = false
     * ObjectUtils.nullSafeEquals(foo, "PUBLIC_STATIC_FINAL_FOO", "foo")         = true
     * ObjectUtils.nullSafeEquals(foo, "PUBLIC_STATIC_NULL_FOO", "foo")          = false
     * </pre>
     *
     * @param source the first object.
     * @param targetPath object target path to be checked.
     * @param value value to compare.
     * @return {@code true} if the objects are same, {@code false} otherwise
     * @throws IllegalAccessException if an Access error occurs.
     * @throws IllegalArgumentException if an Argument error occurs.
     */
    public static boolean nullSafeEquals(final Object source, String targetPath, Object value) throws IllegalAccessException, IllegalArgumentException {
        if (source == null || targetPath == null || targetPath.trim().isEmpty()) {
            return false;
        }
        String[] targetValues = targetPath.split("\\.");
        if (targetValues.length == 0) {
            return false;
        }
        if (source.getClass().getSimpleName().equals(targetValues[0]) && targetPath.contains(".")) {
            targetValues = Arrays.copyOfRange(targetValues, 1, targetValues.length);
        }
        final Field[] fields = source.getClass().getDeclaredFields();
        if (targetValues.length == 1) {
            for (Field field : fields) {
                if (!field.getName().equalsIgnoreCase(targetValues[0])) {
                    continue;
                }

                field.setAccessible(true);
                final Object compareValue = field.get(source);
                if (value == null) {
                    return compareValue == null;
                } else {
                    return value.equals(compareValue);
                }
            }
        } else {
            for (int i=0; i<fields.length; i++) {
                final Field field = fields[i];
                if (!field.getName().equalsIgnoreCase(targetValues[0])) {
                    continue;
                }

                field.setAccessible(true);
                final Object nextSource = field.get(source);
                if (nextSource == null) {
                    return false;
                } else if (targetValues.length >= (i+1)) {
                    return nullSafeEquals(nextSource, targetPath.substring(targetValues[0].length()+1), value);
                }
            }
        }
        return false;
    }

}
