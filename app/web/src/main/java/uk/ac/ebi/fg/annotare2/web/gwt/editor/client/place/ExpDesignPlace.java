/*
 * Copyright 2009-2014 European Molecular Biology Laboratory
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
import com.google.inject.Inject;
import com.google.inject.Provider;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.place.TokenBuilder;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.place.TokenReader;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.place.TokenReaderException;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.view.NotificationPopupPanel;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.ExperimentTab;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.design.ExpDesignSection;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.utils.EnumUtils;

/**
 * @author Olga Melnichuk
 */
public class ExpDesignPlace extends ExperimentPlace {

    private ExpDesignSection expDesignSection;

    public ExpDesignPlace() {
        this(ExpDesignSection.SAMPLES);
    }

    public ExpDesignPlace(ExpDesignSection expDesignSection) {
        setExpDesignSection(expDesignSection);
    }

    public void setExpDesignSection(ExpDesignSection expDesignSection) {
        this.expDesignSection = expDesignSection == null ? ExpDesignSection.SAMPLES : expDesignSection;
    }

    @Override
    public ExperimentTab getSelectedTab() {
        return ExperimentTab.EXP_DESIGN;
    }

    public ExpDesignSection getExpDesignSection() {
        return expDesignSection;
    }

    @Prefix("DESIGN")
    public static class Tokenizer implements PlaceTokenizer<ExpDesignPlace> {

        private final Provider<ExpDesignPlace> placeProvider;

        @Inject
        public Tokenizer(Provider<ExpDesignPlace> placeProvider) {
            this.placeProvider = placeProvider;
        }

        public String getToken(ExpDesignPlace place) {
            return new TokenBuilder()
                    .add(place.getExpDesignSection().name())
                    .toString();
        }

        public ExpDesignPlace getPlace(String token) {
            TokenReader reader = new TokenReader(token);
            try {
                ExpDesignPlace place = placeProvider.get();
                String sectionToken = reader.nextString();
                ExpDesignSection section = EnumUtils.getIfPresent(ExpDesignSection.class, sectionToken);
                if (section == null) {
                    throw new TokenReaderException("Unrecognized token: " + sectionToken);
                }
                place.setExpDesignSection(section);
                return place;
            } catch (TokenReaderException e) {
                NotificationPopupPanel.failure("Exception caught", e);
                return null;
            }
        }
    }
}
