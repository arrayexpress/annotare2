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

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.inject.Provider;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.place.TokenBuilder;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.place.TokenReader;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.place.TokenReaderException;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.arraydesign.AdfSection;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.arraydesign.ArrayDesignTab;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.utils.EnumUtils;

/**
 * @author Olga Melnichuk
 */
public class AdHeaderPlace extends ArrayDesignPlace {

    private AdfSection section;

    public AdHeaderPlace() {
        setSection(null);
    }

    public AdHeaderPlace(AdfSection section) {
        setSection(section);
    }

    public AdfSection getSection() {
        return section;
    }

    public void setSection(AdfSection section) {
        this.section = section == null ? AdfSection.GENERAL_INFO : section;
    }

    @Override
    public ArrayDesignTab getSelectedTab() {
        return ArrayDesignTab.Header;
    }

    @Prefix("adfHeader")
    public static class Tokenizer implements PlaceTokenizer<AdHeaderPlace> {

        private final Provider<AdHeaderPlace> placeProvider;

        @Inject
        public Tokenizer(Provider<AdHeaderPlace> placeProvider) {
            this.placeProvider = placeProvider;
        }

        public String getToken(AdHeaderPlace place) {
            return new TokenBuilder()
                    .add(place.getSection().name())
                    .toString();
        }

        public AdHeaderPlace getPlace(String token) {
            TokenReader reader = new TokenReader(token);
            try {
                AdHeaderPlace place = placeProvider.get();

                String sectionToken = reader.nextString();

                AdfSection section = EnumUtils.getIfPresent(AdfSection.class, sectionToken);
                if (section == null) {
                    throw new TokenReaderException("Unrecognized IDF section token: " + sectionToken);
                }

                place.setSection(section);
                return place;
            } catch (TokenReaderException e) {
                //TODO log
                Window.alert(e.getMessage());
                return null;
            }
        }
    }
}
