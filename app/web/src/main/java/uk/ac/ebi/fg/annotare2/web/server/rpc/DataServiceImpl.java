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
import uk.ac.ebi.fg.annotare2.services.efo.EfoNode;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.DataService;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ArrayDesignRef;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.SystemUsedEfoTerms;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.dto.EfoTermDto;
import uk.ac.ebi.fg.annotare2.web.server.services.AnnotareEfoService;
import uk.ac.ebi.fg.annotare2.web.server.services.ae.AE;
import uk.ac.ebi.fg.annotare2.web.server.services.ae.ArrayExpressArrayDesignList;

import javax.annotation.Nullable;
import java.util.*;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static uk.ac.ebi.fg.annotare2.web.server.rpc.transform.UIObjectConverter.uiEfoTerms;

/**
 * @author Olga Melnichuk
 */
public class DataServiceImpl extends RemoteServiceServlet implements DataService {

    //TODO move to config
    private static class FutureProperties {
        public String getOrganismPartAccession() {
            return "EFO_0000635";
        }

        public String getOrganismTermAccession() {
            return "OBI_0100026";
        }

        public String getUnitTermAccession() {
            return "UO_0000000";
        }

    }
/*
    private static final String unitAccession = "UO_0000000";
    private static final String organismAccession = "OBI_0100026";
    private static final String organismPartAccession = "EFO_0000635";
*/
    private static enum SystemEfoTermType {
    ORGANISM {
        @Override
        protected String getAccession(FutureProperties properties) {
            return properties.getOrganismTermAccession();
        }
    },
    ORGANISM_PART {
        @Override
        protected String getAccession(FutureProperties properties) {
            return properties.getOrganismPartAccession();
        }
    },
    UNIT {
        @Override
        protected String getAccession(FutureProperties properties) {
            return properties.getUnitTermAccession();
        }
    };

    protected abstract String getAccession(FutureProperties properties);

   /* public static Map<SystemEfoTerm, String> accessionMap(FutureProperties properties) {
        Map<SystemEfoTerm, String> map = newHashMap();
        for(SystemEfoTerm term : values()) {
            map.put(term, term.getAccession(properties));
        }
        return map;
    }
    */
}


    private final ArrayExpressArrayDesignList adList;
    private final AnnotareEfoService efoService;
    private final FutureProperties properties = new FutureProperties();


    @Inject
    public DataServiceImpl(ArrayExpressArrayDesignList adList, AnnotareEfoService efoService) {
        this.adList = adList;
        this.efoService = efoService;
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

    public SystemUsedEfoTerms getSystemEfoTerms() {
        SystemUsedEfoTerms terms = new SystemUsedEfoTerms();
        for(SystemEfoTermType termType : SystemEfoTermType.values()) {
            EfoNode term = efoService.findTermByAccession(termType.getAccession(properties));
            //TODO

        }
        return null;
    }
}
