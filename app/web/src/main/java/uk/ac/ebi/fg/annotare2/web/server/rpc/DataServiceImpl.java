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

package uk.ac.ebi.fg.annotare2.web.server.rpc;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fg.annotare2.magetabcheck.efo.EfoTerm;
import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfileType;
import uk.ac.ebi.fg.annotare2.submission.model.OntologyTerm;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.DataService;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.*;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ProtocolType;
import uk.ac.ebi.fg.annotare2.web.server.ProtocolTypes;
import uk.ac.ebi.fg.annotare2.web.server.properties.AnnotareProperties;
import uk.ac.ebi.fg.annotare2.web.server.services.AnnotareEfoService;
import uk.ac.ebi.fg.annotare2.web.server.services.EmailSender;
import uk.ac.ebi.fg.annotare2.web.server.services.ae.ArrayExpress;
import uk.ac.ebi.fg.annotare2.web.server.services.ae.ArrayExpressArrayDesignList;

import javax.annotation.Nullable;
import java.util.*;

import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.Collections2.transform;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.intersection;
import static com.google.common.collect.Sets.newHashSet;
import static uk.ac.ebi.fg.annotare2.web.server.rpc.transform.UIObjectConverter.uiEfoTerm;
import static uk.ac.ebi.fg.annotare2.web.server.rpc.transform.UIObjectConverter.uiEfoTerms;

/**
 * @author Olga Melnichuk
 */
public class DataServiceImpl extends ErrorReportingRemoteServiceServlet implements DataService {

    private static final Logger log = LoggerFactory.getLogger(DataServiceImpl.class);

    private final ArrayExpressArrayDesignList arrayDesignList;

    private final AnnotareEfoService efoService;
    private final AnnotareProperties properties;
    private final ProtocolTypes protocolTypes;

    @Inject
    public DataServiceImpl(ArrayExpressArrayDesignList arrayDesignList,
                           ProtocolTypes protocolTypes,
                           AnnotareEfoService efoService,
                           AnnotareProperties properties,
                           EmailSender emailSender) {
        super(emailSender);
        this.arrayDesignList = arrayDesignList;
        this.efoService = efoService;
        this.properties = properties;
        this.protocolTypes = protocolTypes;
    }

    @Override
    public List<ArrayDesignRef> getArrayDesignList(String query, int limit) {
        return newArrayList(Iterables.transform(Iterables.limit(arrayDesignList.getArrayDesigns(query), limit), new Function<ArrayExpress.ArrayDesign, ArrayDesignRef>() {
            @Nullable
            @Override
            public ArrayDesignRef apply(@Nullable ArrayExpress.ArrayDesign ad) {
                return new ArrayDesignRef(ad.getAccession(), ad.getName());
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
    public OntologyTerm getEfoTermByLabel(String label) {
        return uiEfoTerm(efoService.findTermByLabel(label));
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
                log.error("Unable to load system term by accession: " + accession);
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
                types.add(new ProtocolType(uiEfoTerm(term), typeConfig.getDefinition(), typeConfig.getSubjectType()));
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

    @Override
    public List<String> getAeExperimentTypes(ExperimentProfileType type) {
        Set<String> allTypes = newHashSet(transformed(getDescendantTerms(SystemEfoTerm.AE_EXPERIMENT_TYPE, 100)));
        Set<String> profileSpecificTypes = newHashSet(transformed(getDescendantTerms(type.isMicroarray() ? SystemEfoTerm.ARRAY_ASSAY : SystemEfoTerm.SEQUENCING_ASSAY, 100)));

        return new ArrayList<String>(sorted(intersection(allTypes, profileSpecificTypes)));
    }

    @Override
    public List<String> getMaterialTypes() {
        return new ArrayList<String>(properties.getMaterialTypes());
    }

    @Override
    public List<String> getSequencingHardware() {
        return new ArrayList<String>(properties.getSequencingHardware());
    }

    @Override
    public List<OntologyTermGroup> getExperimentalDesigns() {
        Collection<EfoTerm> subTerms = sortedByTermLabel(getChildTerms(SystemEfoTerm.STUDY_DESIGN, 100));

        List<OntologyTermGroup> groups = new ArrayList<OntologyTermGroup>();
        for (EfoTerm subTerm : subTerms) {
            Collection<EfoTerm> descendants = sortedByTermLabel(efoService.getDescendantTerms(subTerm, 100));

            OntologyTermGroup group = new OntologyTermGroup(subTerm.getLabel());
            for (EfoTerm descendant : descendants) {
                if (descendant.isOrganisational()) {
                    continue;
                }
                group.add(uiEfoTerm(descendant), descendant.getDefinition());
            }
            groups.add(group);
        }
        return groups;
    }

    private Collection<EfoTerm> sortedByTermLabel(Collection<EfoTerm> terms) {
        return Ordering.natural().onResultOf(new Function<EfoTerm, String>() {
            @Override
            public String apply(EfoTerm term) {
                return null != term ? nullToEmpty(term.getLabel()).toLowerCase() : null;
            }
        }).sortedCopy(terms);
    }

    private Collection<String> sorted(Collection<String> terms) {
        return Ordering.natural().onResultOf(new Function<String, String>() {
            @Override
            public String apply(String term) {
                return nullToEmpty(term).toLowerCase();
            }
        }).sortedCopy(terms);
    }

    private Collection<String> transformed(Collection<EfoTerm> terms) {
        return transform(terms, new Function<EfoTerm, String>() {
            @Nullable
            @Override
            public String apply(@Nullable EfoTerm efoTerm) {
                return null != efoTerm ? efoTerm.getLabel() : "";
            }
        });
    }
    private OntologyTerm loadSystemTerm(String accession) {
        return uiEfoTerm(efoService.findTermByAccession(accession));
    }

    private EfoTerm getSystemTerm(SystemEfoTerm systemEfoTerm) {
        EfoTerm sysTerm = efoService.findTermByAccession(properties.getEfoTermAccession(systemEfoTerm));
        if (null == sysTerm) {
            log.error("Unable to find system EFO term: " + systemEfoTerm);
        }
        return sysTerm;
    }

    private Collection<EfoTerm> getChildTerms(SystemEfoTerm baseEfoTerm, int limit) {
        EfoTerm term = getSystemTerm(baseEfoTerm);
        if (null == term) {
            return Collections.emptyList();
        }
        return efoService.getChildTerms(term, limit);
    }

    private Collection<EfoTerm> getDescendantTerms(SystemEfoTerm baseEfoTerm, int limit) {
        EfoTerm term = getSystemTerm(baseEfoTerm);
        if (null == term) {
            return Collections.emptyList();
        }
        return efoService.getDescendantTerms(term, limit);
    }
}
