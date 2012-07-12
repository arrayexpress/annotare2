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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.mvp;

import com.google.inject.Inject;
import com.google.inject.Provider;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.place.IdfPlace;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.place.SdrfPlace;

/**
 * @author Olga Melnichuk
 */
public class EditorAppPlaceFactory {

    @Inject
    IdfPlace.Tokenizer idfPlaceTokenizer;

    @Inject
    Provider<IdfPlace> idfPlaceProvider;

    @Inject
    SdrfPlace.Tokenizer sdrfPlaceTokenizer;

    @Inject
    Provider<SdrfPlace> sdrfPlaceProvider;

    public IdfPlace.Tokenizer getIdfPlaceTokenizer() {
        return idfPlaceTokenizer;
    }

    public IdfPlace getIdfPlace() {
        return idfPlaceProvider.get();
    }

    public SdrfPlace.Tokenizer getSdrfPlaceTokenizer() {
        return sdrfPlaceTokenizer;
    }

    public SdrfPlace getSdrfPlace() {
        return sdrfPlaceProvider.get();
    }

}
