/*
 * Copyright 2009-2014 European Molecular Biology Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or impl
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.web.gwt.common.shared.util;

/**
 * @author Olga Melnichuk
 */
final class NoneValue extends ValueRange<Object> {

    static final NoneValue INSTANCE = new NoneValue();

    private NoneValue() {
    }

    @Override
    public boolean isAny() {
        return false;
    }

    @Override
    public boolean isNone() {
        return true;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    @Override
    public Object get() {
        throw new IllegalStateException("Unable to call get() on NONE value range");
    }

    @Override
    public int hashCode() {
        return INSTANCE.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this;
    }

    private Object readResolve() {
        return INSTANCE;
    }

    private static final long serialVersionUID = 0;
}
