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

package me.phoboslabs.illuminati.util.objectutil;

import java.util.Arrays;
import java.util.stream.IntStream;

public class CompareString implements CharSequence {

    private final char[] stringCharArray;

    public CompareString(char[] stringCharArray) {
        if (stringCharArray == null) {
            throw new IllegalArgumentException("The stringCharArray must not be null.");
        }
        this.stringCharArray = stringCharArray;
    }

    @Override
    public int length() {
        return this.stringCharArray.length;
    }

    @Override
    public char charAt(int index) {
        return this.stringCharArray[index];
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        throw new UnsupportedOperationException();
    }

    public void clear() {
        Arrays.fill(this.stringCharArray, '\u0000');
    }

    public char[] toCharArray() {
        return this.stringCharArray;
    }

    public char[] toCharArrayByIgnoreCase() {
        char[] ignoreCaseArray = new char[this.length()];
        IntStream.range(0, this.length()).forEach(i -> ignoreCaseArray[i] = Character.toLowerCase(this.stringCharArray[i]));
        return ignoreCaseArray;
    }
}
