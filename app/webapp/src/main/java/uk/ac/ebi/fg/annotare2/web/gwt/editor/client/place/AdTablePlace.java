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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.place;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.arraydesign.ArrayDesignTab;

/**
 * @author Olga Melnichuk
 */
public class AdTablePlace extends ArrayDesignPlace {

    @Override
    public ArrayDesignTab getSelectedTab() {
        return ArrayDesignTab.Table;
    }

    @Prefix("ADF_TABLE")
    public static class Tokenizer implements PlaceTokenizer<AdTablePlace> {

        public String getToken(AdTablePlace place) {
            return "0";
        }

        public AdTablePlace getPlace(String token) {
            return new AdTablePlace();
        }
    }
}
