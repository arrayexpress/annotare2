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

package uk.ac.ebi.fg.annotare2.web.server.rpc;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.magetabcheck.extension.KnownTermSource;
import uk.ac.ebi.fg.annotare2.services.efo.EfoService;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.VocabularyService;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.idf.UITerm;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.idf.UITermSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class VocabularyServiceImpl extends RemoteServiceServlet implements VocabularyService {

    private final EfoService efoService;

    @Inject
    public VocabularyServiceImpl(EfoService efoService) {
        this.efoService = efoService;
    }

    @Override
    public ArrayList<UITerm> getExperimentalDesigns() {
        ArrayList<UITerm> list = new ArrayList<UITerm>();
        //TODO ask for term names from efo
        list.addAll(getFromEfo("biological variation design", EfoService.BIOLOGICAL_VARIATION_DESINGS));
        list.addAll(getFromEfo("methodological variation design", EfoService.METHODOLOGICAL_VARIATION_DESIGNS));
        list.addAll(getFromEfo("biomolecular annotation design", EfoService.BIOMOLECULAR_ANNOTATION_DESIGNS));
        return list;
    }

    private Collection<? extends UITerm> getFromEfo(String parentName, String parentAccession) {
        UITermSource efo = convert(KnownTermSource.EFO);
        List<UITerm> list = new ArrayList<UITerm>();
        Collection<String> termNames = efoService.getSubTermsOf(parentAccession);
        for (String name : termNames) {
            list.add(new UITerm(name, "", efo, parentName));
        }
        return list;
    }

    @Override
    public ArrayList<UITermSource> getTermSources() {
        ArrayList<UITermSource> list = new ArrayList<UITermSource>();
        for (KnownTermSource ts : KnownTermSource.values()) {
            list.add(convert(ts));
        }
        return list;
    }

    private UITermSource convert(KnownTermSource def) {
        return new UITermSource(
                def.getName(),
                "",
                def.getUrl(),
                def.getDescription());
    }

}
