/*
 * Copyright 2009-2013 European Molecular Biology Laboratory
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

package uk.ac.ebi.fg.annotare2.magetab.rowbased;

import com.google.common.annotations.GwtCompatible;
import uk.ac.ebi.fg.annotare2.magetab.table.Row;

/**
 * @author Olga Melnichuk
 */
@GwtCompatible
public class AdfInfo {

    private Row.Cell<String> arrayDesignName;

    private Row.Cell<String> version;

    private Row.Cell<String> provider;

    private Row.Cell<String> printingProtocol;

    public Row.Cell<String> getArrayDesignName() {
        return arrayDesignName;
    }

    public void setArrayDesignName(Row.Cell<String> arrayDesignName) {
        this.arrayDesignName = arrayDesignName;
    }

    public Row.Cell<String> getVersion() {
        return version;
    }

    public void setVersion(Row.Cell<String> version) {
        this.version = version;
    }

    public Row.Cell<String> getProvider() {
        return provider;
    }

    public void setProvider(Row.Cell<String> provider) {
        this.provider = provider;
    }

    public Row.Cell<String> getPrintingProtocol() {
        return printingProtocol;
    }

    public void setPrintingProtocol(Row.Cell<String> printingProtocol) {
        this.printingProtocol = printingProtocol;
    }
}
