/*
 * Copyright 2009-2016 European Molecular Biology Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.web.gwt.common.shared.util;

/**
 * @author Olga Melnichuk
 */
final class SingletonValue<T> extends ValueRange<T> {

    private T value;

    private SingletonValue() {
        /* required by serialization */
    }

    SingletonValue(T value) {
        this.value = value;
    }

    @Override
    public boolean isAny() {
        return false;
    }

    @Override
    public boolean isNone() {
        return false;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public T get() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SingletonValue that = (SingletonValue) o;

        if (!value.equals(that.value)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    private static final long serialVersionUID = 0;
}
