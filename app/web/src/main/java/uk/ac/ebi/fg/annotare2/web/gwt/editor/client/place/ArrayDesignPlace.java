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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;
import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * @author Olga Melnichuk
 */
public class ArrayDesignPlace extends Place {

    private String token;

    @Prefix("adfView")
    public static class Tokenizer implements PlaceTokenizer<ArrayDesignPlace> {

        private final Provider<ArrayDesignPlace> placeProvider;

        @Inject
        public Tokenizer(Provider<ArrayDesignPlace> placeProvider) {
            this.placeProvider = placeProvider;
        }

        public String getToken(ArrayDesignPlace place) {
            return place.token;
        }

        public ArrayDesignPlace getPlace(String token) {
            ArrayDesignPlace place = placeProvider.get();
            place.token  = token;
            return place;
        }
    }
}
