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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.mvp;

import com.google.inject.Inject;
import com.google.inject.Provider;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.place.*;

/**
 * @author Olga Melnichuk
 */
public class EditorPlaceFactory {

    @Inject
    ExpInfoPlace.Tokenizer expInfoPlaceTokenizer;
    @Inject
    ExpDesignPlace.Tokenizer expDesignPlaceTokenizer;
    @Inject
    IdfPreviewPlace.Tokenizer idfPreviewPlaceTokenizer;
    @Inject
    SdrfPreviewPlace.Tokenizer sdrfPreviewPlaceTokenizer;
    @Inject
    AdHeaderPlace.Tokenizer adHeaderPlaceTokenizer;
    @Inject
    AdTablePlace.Tokenizer adTablePlaceTokenizer;

    @Inject
    Provider<ExpInfoPlace> expInfoPlaceProvider;
    @Inject
    Provider<ExpDesignPlace> expDesignPlaceProvider;
    @Inject
    Provider<IdfPreviewPlace> idfPreviewPlaceProvider;
    @Inject
    Provider<SdrfPreviewPlace> sdrfPreviewPlaceProvider;
    @Inject
    Provider<AdHeaderPlace> adHeaderPlaceProvider;
    @Inject
    Provider<AdTablePlace> adTablePlaceProvider;

    public ExpInfoPlace getExpInfoPlace() {
        return expInfoPlaceProvider.get();
    }

    public AdHeaderPlace getAdHeaderPlace() {
        return adHeaderPlaceProvider.get();
    }

    public ExpInfoPlace.Tokenizer getExpInfoPlaceTokenizer() {
        return expInfoPlaceTokenizer;
    }

    public ExpDesignPlace.Tokenizer getExpDesignPlaceTokenizer() {
        return expDesignPlaceTokenizer;
    }

    public IdfPreviewPlace.Tokenizer getIdfPreviewPlaceTokenizer() {
        return idfPreviewPlaceTokenizer;
    }

    public SdrfPreviewPlace.Tokenizer getSdrfPreviewPlaceTokenizer() {
        return sdrfPreviewPlaceTokenizer;
    }

    public AdHeaderPlace.Tokenizer getAdHeaderPlaceTokenizer() {
        return adHeaderPlaceTokenizer;
    }

    public AdTablePlace.Tokenizer getAdTablePlaceTokenizer() {
        return adTablePlaceTokenizer;
    }
}
