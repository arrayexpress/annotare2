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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or impl
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.web.gwt.common.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author Olga Melnichuk
 */
public class Accession implements IsSerializable {

    private static final String UNACCESSIONED = "UNACCESSIONED";

    private String value;

    public Accession() {
    }

    public Accession(String value) {
        this.value = value;
    }

    public String getText() {
        return hasValue() ? value : UNACCESSIONED;
    }

    public String getValue() {
        return value;
    }

    public final boolean hasValue() {
        return value != null && value.trim().length() > 0;
    }

    @Override
    public String toString() {
        return getText();
    }
}
