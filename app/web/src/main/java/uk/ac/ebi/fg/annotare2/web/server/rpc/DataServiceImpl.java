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
import uk.ac.ebi.fg.annotare2.configmodel.ExperimentProfileType;
import uk.ac.ebi.fg.annotare2.configmodel.OntologyTerm;
import uk.ac.ebi.fg.annotare2.magetabcheck.efo.EfoTerm;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.DataService;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ApplicationProperties;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ArrayDesignRef;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.SystemEfoTerm;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.SystemEfoTermMap;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ProtocolType;
import uk.ac.ebi.fg.annotare2.web.server.properties.AnnotareProperties;
import uk.ac.ebi.fg.annotare2.web.server.ProtocolTypes;
import uk.ac.ebi.fg.annotare2.web.server.services.AnnotareEfoService;
import uk.ac.ebi.fg.annotare2.web.server.services.ae.AE;
import uk.ac.ebi.fg.annotare2.web.server.services.ae.ArrayExpressArrayDesignList;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
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
    private final ProtocolTypes protocolTypes;

    @Inject
    public DataServiceImpl(ArrayExpressArrayDesignList adList,
                           AnnotareEfoService efoService,
                           AnnotareProperties properties) {
        this.adList = adList;
        this.efoService = efoService;
        this.properties = properties;
        this.protocolTypes = ProtocolTypes.create();
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
        for (SystemEfoTerm systemTerm : SystemEfoTerm.values()) {
            String accession = properties.getEfoTermAccession(systemTerm);
            if (accession == null) {
                log.error("application properties do not contain accession for a system term: " + systemTerm);
                continue;
            }

            OntologyTerm term = loadSystemTerm(accession);
            if (term == null) {
                log.error("can't load system term by accession: " + accession);
                continue;
            }

            map.put(systemTerm, term);
        }
        return map;
    }

    @Override
    public List<ProtocolType> getProtocolTypes(ExperimentProfileType expType) {
        List<ProtocolType> types = newArrayList();
        for (ProtocolTypes.Config typeConfig : protocolTypes.filter(expType)) {
            EfoTerm term = efoService.findTermByAccession(typeConfig.getId());
            if (term == null) {
                log.error("Protocol Type (" + typeConfig.getId() + ") not found in EFO");
            } else {
                //TODO get definition from the term not from config
                types.add(new ProtocolType(uiEfoTerm(term), typeConfig.getDefinition(), typeConfig.getTargetType()));
            }
        }
        return types;
    }

    @Override
    public List<OntologyTerm> getContactRoles() {
        Collection<String> accessions = properties.getContactRoleAccessions();
        List<OntologyTerm> terms = new ArrayList<OntologyTerm>();
        for (String accession : accessions) {
            EfoTerm term = efoService.findTermByAccession(accession);
            if (term == null) {
                log.error("Contact Role (" + accession + ") not found in EFO");
            } else {
                terms.add(uiEfoTerm(term));
            }
        }
        return terms;
    }

    @Override
    public ApplicationProperties getApplicationProperties() {
        return new ApplicationProperties.Builder()
                .setFtpUrl(properties.getPublicFtpUrl())
                .setFtpUsername(properties.getPublicFtpUsername())
                .setFtpPassword(properties.getPublicFtpPassword())
                .build();
    }

    private OntologyTerm loadSystemTerm(String accession) {
        return uiEfoTerm(efoService.findTermByAccession(accession));
    }
}
