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

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fg.annotare2.configmodel.OntologyTerm;
import uk.ac.ebi.fg.annotare2.services.efo.EfoTerm;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.DataService;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ArrayDesignRef;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.SystemEfoTerm;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.SystemEfoTermMap;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.dto.EfoGraphDto;
import uk.ac.ebi.fg.annotare2.web.server.AnnotareProperties;
import uk.ac.ebi.fg.annotare2.web.server.services.AnnotareEfoService;
import uk.ac.ebi.fg.annotare2.web.server.services.ae.AE;
import uk.ac.ebi.fg.annotare2.web.server.services.ae.ArrayExpressArrayDesignList;

import javax.annotation.Nullable;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static uk.ac.ebi.fg.annotare2.web.server.rpc.transform.UIObjectConverter.*;

/**
 * @author Olga Melnichuk
 */
public class DataServiceImpl extends RemoteServiceServlet implements DataService {

    private static final Logger log = LoggerFactory.getLogger(DataServiceImpl.class);

    private final ArrayExpressArrayDesignList adList;
    private final AnnotareEfoService efoService;
    private final AnnotareProperties properties;

    @Inject
    public DataServiceImpl(ArrayExpressArrayDesignList adList,
                           AnnotareEfoService efoService,
                           AnnotareProperties properties) {
        this.adList = adList;
        this.efoService = efoService;
        this.properties = properties;
    }

    @Override
    public List<ArrayDesignRef> getArrayDesignList(String query, int limit) {
        return newArrayList(Iterables.transform(Iterables.limit(adList.getArrayDesigns(query), limit), new Function<AE.ArrayDesign, ArrayDesignRef>() {
            @Nullable
            @Override
            public ArrayDesignRef apply(@Nullable AE.ArrayDesign ad) {
                return new ArrayDesignRef(ad.getName(), ad.getDesription());
            }
        }));
    }

    @Override
    public List<OntologyTerm> getEfoTerms(String query, int limit) {
        return uiEfoTerms(efoService.suggest(query, limit));
    }

    @Override
    public List<OntologyTerm> getEfoTerms(String query, String rootAccession, int limit) {
        return uiEfoTerms(efoService.suggest(query, rootAccession, limit));
    }

    @Override
    public SystemEfoTermMap getSystemEfoTerms() {
        SystemEfoTermMap map = new SystemEfoTermMap();
        for(SystemEfoTerm systemTerm : SystemEfoTerm.values()) {
            String accession = properties.getEfoTermAccession(systemTerm);
            if (accession == null) {
                log.error("application properties do not contain accession for a system term: " + systemTerm);
                continue;
            }
            map.put(systemTerm, loadSystemTerm(accession));
        }
        return map;
    }

    @Override
    public EfoGraphDto getProtocolTypes() {
        return uiEfoGraph(efoService.getProtocolTypes());
    }

    private OntologyTerm loadSystemTerm(String accession) {
        EfoTerm term = efoService.findTermByAccession(accession);
        if (term == null) {
            log.error("Can't find system used EFO term: " + accession);
        }
        return uiEfoTerm(term);
    }
}
