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
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.idf.ExpInfoSection;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.utils.EnumUtils;

/**
 * @author Olga Melnichuk
 */
public class ExpInfoPlace extends ExperimentPlace {

    private ExpInfoSection expInfoSection;

    public ExpInfoPlace() {
        setExpInfoSection(null);
    }

    public ExpInfoPlace(ExpInfoSection expInfoSection) {
        setExpInfoSection(expInfoSection);
    }

    public ExpInfoPlace(ExpInfoPlace other) {
        setExpInfoSection(other.getExpInfoSection());
    }

    public ExpInfoSection getExpInfoSection() {
        return expInfoSection;
    }

    public void setExpInfoSection(ExpInfoSection expInfoSection) {
        this.expInfoSection = expInfoSection == null ?  ExpInfoSection.GENERAL_INFO : expInfoSection;
    }

    @Override
    public ExperimentTab getSelectedTab() {
        return ExperimentTab.EXP_INFO;
    }

    @Prefix("INFO")
    public static class Tokenizer implements PlaceTokenizer<ExpInfoPlace> {

        private final Provider<ExpInfoPlace> placeProvider;

        @Inject
        public Tokenizer(Provider<ExpInfoPlace> placeProvider) {
            this.placeProvider = placeProvider;
        }

        public String getToken(ExpInfoPlace place) {
            return new TokenBuilder()
                    .add(place.getExpInfoSection().name())
                    .toString();
        }

        public ExpInfoPlace getPlace(String token) {
            TokenReader reader = new TokenReader(token);
            try {
                ExpInfoPlace place = placeProvider.get();
                String sectionToken = reader.nextString();
                ExpInfoSection section = EnumUtils.getIfPresent(ExpInfoSection.class, reader.nextString());
                if (section == null) {
                    throw new TokenReaderException("Unrecognized token: " + sectionToken);
                }
                place.setExpInfoSection(section);
                return place;
            } catch (TokenReaderException e) {
                //TODO log
                Window.alert(e.getMessage());
                return null;
            }
        }
    }
}
