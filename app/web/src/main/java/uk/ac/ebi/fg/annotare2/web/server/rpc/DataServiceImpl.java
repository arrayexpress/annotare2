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
import uk.ac.ebi.fg.annotare2.services.efo.EfoNode;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.DataService;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ArrayDesignRef;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.SystemEfoTermsDto;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.dto.EfoTermDto;
import uk.ac.ebi.fg.annotare2.web.server.AnnotareProperties;
import uk.ac.ebi.fg.annotare2.web.server.services.AnnotareEfoService;
import uk.ac.ebi.fg.annotare2.web.server.services.ae.AE;
import uk.ac.ebi.fg.annotare2.web.server.services.ae.ArrayExpressArrayDesignList;

import javax.annotation.Nullable;
import java.util.*;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static uk.ac.ebi.fg.annotare2.web.server.rpc.transform.UIObjectConverter.uiEfoTerm;
import static uk.ac.ebi.fg.annotare2.web.server.rpc.transform.UIObjectConverter.uiEfoTerms;

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
    public List<EfoTermDto> getEfoTerms(String query, int limit) {
        return uiEfoTerms(efoService.suggest(query, limit));
    }

    @Override
    public List<EfoTermDto> getEfoTerms(String query, String rootAccession, int limit) {
        return uiEfoTerms(efoService.suggest(query, rootAccession, limit));
    }

    @Override
    public SystemEfoTermsDto getSystemEfoTerms() {
        SystemEfoTermsDto dto = new SystemEfoTermsDto();
        dto.setOrganismTerm(
                loadSystemTerm(properties.getOrganismTermAccession()));
        dto.setOrganismPartTerm(
                loadSystemTerm(properties.getOrganismPartAccession()));
        dto.setUnitTerm(
                loadSystemTerm(properties.getUnitTermAccession()));
        dto.setMaterialTypeTerm(
                loadSystemTerm(properties.getMaterialTypeTermAccession()));
        dto.setStudyDesignTerm(
                loadSystemTerm(properties.getStudyDesignAccession()));
        return dto;
    }

    private EfoTermDto loadSystemTerm(String accession) {
        EfoNode term = efoService.findTermByAccession(accession);
        if (term == null) {
            log.error("Can't find system used EFO term: " + accession);
        }
        return uiEfoTerm(term);
    }
}
