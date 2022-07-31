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

package me.phoboslabs.illuminati.common.util.exception;

/**
 * Created by leekyoungil (leekyoungil@gmail.com) on 08/22/2021.
 */
public class ServerIsNotAvailableException extends RuntimeException {

    private Throwable cause;

    public ServerIsNotAvailableException() {
        super();
    }

    public ServerIsNotAvailableException(String s) {
        super(s);
    }

    public ServerIsNotAvailableException(String s, Throwable throwable) {
        super(s);
        this.cause = throwable;
    }

    public ServerIsNotAvailableException(Throwable throwable) {
        super(throwable.toString());
        this.cause = throwable;
    }

    public Throwable getCause() {
        return this.cause;
    }
}
