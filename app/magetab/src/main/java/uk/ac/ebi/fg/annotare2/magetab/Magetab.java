/*
 * Copyright 2009-2012 European Molecular Biology Laboratory
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

package uk.ac.ebi.fg.annotare2.magetab;

import uk.ac.ebi.fg.annotare2.magetab.idf.format.JseTextFormatter;

/**
 * Magetab library can be used in both JSE and GWT environments. This class helps to initialise JSE specific code;
 * <code>Magetab.init()</code> should be run in your application's initialisation routine.
 * <p/>
 * Note: To use Magetab in GWT use <code>GwtMagetab.init()</code> from the corresponding <code>magetab-gwt</code>
 * library.
 *
 * @author Olga Melnichuk
 */
public class Magetab {

    public void init() {
        JseTextFormatter.init();
    }
}
