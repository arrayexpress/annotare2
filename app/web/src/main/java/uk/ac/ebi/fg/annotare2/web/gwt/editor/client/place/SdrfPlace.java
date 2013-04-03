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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.place;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.inject.Provider;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.place.TokenBuilder;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.place.TokenReader;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.place.TokenReaderException;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.ExperimentTab;

/**
 * @author Olga Melnichuk
 */
public class SdrfPlace extends ExperimentPlace {

    private boolean sheetModeOn;

    public SdrfPlace() {
    }

    public SdrfPlace(SdrfPlace other) {
        setSheetModeOn(other.isSheetModeOn());
    }

    public boolean isSheetModeOn() {
        return sheetModeOn;
    }

    public void setSheetModeOn(boolean on) {
        this.sheetModeOn = on;
    }

    public ExperimentTab getSelectedTab() {
        return ExperimentTab.EXP_DESIGN;
    }

    @Prefix("sdrfView")
    public static class Tokenizer implements PlaceTokenizer<SdrfPlace> {

        private final Provider<SdrfPlace> placeProvider;

        @Inject
        public Tokenizer(Provider<SdrfPlace> placeProvider) {
            this.placeProvider = placeProvider;
        }

        public String getToken(SdrfPlace place) {
            return new TokenBuilder()
                    .add(place.isSheetModeOn())
                    .toString();
        }

        public SdrfPlace getPlace(String token) {
            TokenReader reader = new TokenReader(token);
            try {
                SdrfPlace place = placeProvider.get();
                place.setSheetModeOn(reader.nextBoolean());
                return place;
            } catch (TokenReaderException e) {
                //TODO log
                Window.alert(e.getMessage());
                return null;
            }
        }
    }
}
