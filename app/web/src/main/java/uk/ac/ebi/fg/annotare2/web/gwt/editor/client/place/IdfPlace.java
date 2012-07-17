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
import com.google.inject.Inject;
import com.google.inject.Provider;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.EditorTabType;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.idf.IdfSection;

/**
 * @author Olga Melnichuk
 */
public class IdfPlace extends EditorPlace {

    private IdfSection idfSection;

    public IdfPlace() {
        setIdfSection(null);
    }

    public IdfPlace(IdfSection idfSection) {
        setIdfSection(idfSection);
    }

    public IdfSection getIdfSection() {
        return idfSection;
    }

    public void setIdfSection(IdfSection idfSection) {
        setIdfSection(idfSection, IdfSection.GENERAL_INFO);
    }

    public void setIdfSection(IdfSection idfSection, IdfSection defaultValue) {
        this.idfSection = idfSection == null ? defaultValue : idfSection;
    }

    public EditorTabType getTabType() {
        return EditorTabType.IDF;
    }

    @Prefix("idfEdit")
    public static class Tokenizer implements PlaceTokenizer<IdfPlace> {

        private final Provider<IdfPlace> placeProvider;

        @Inject
        public Tokenizer(Provider<IdfPlace> placeProvider) {
            this.placeProvider = placeProvider;
        }

        public String getToken(IdfPlace place) {
            return place.getIdfSection().name();
        }

        public IdfPlace getPlace(String token) {
            IdfSection section = IdfSection.getIfPresent(token);
            if (section == null) {
                return null;
            }
            IdfPlace place = placeProvider.get();
            place.setIdfSection(section);
            return place;
        }
    }
}
