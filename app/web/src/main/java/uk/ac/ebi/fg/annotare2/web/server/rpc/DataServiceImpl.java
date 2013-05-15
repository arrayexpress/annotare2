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
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.DataService;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ArrayDesignRef;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.dto.EfoTermDto;
import uk.ac.ebi.fg.annotare2.web.server.services.AnnotareEfoService;
import uk.ac.ebi.fg.annotare2.web.server.services.ae.AE;
import uk.ac.ebi.fg.annotare2.web.server.services.ae.ArrayExpressArrayDesignList;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @author Olga Melnichuk
 */
public class DataServiceImpl extends RemoteServiceServlet implements DataService {

    private final ArrayExpressArrayDesignList adList;
    private final AnnotareEfoService efoService;

    @Inject
    public DataServiceImpl(ArrayExpressArrayDesignList adList, AnnotareEfoService efoService) {
        this.adList = adList;
        this.efoService = efoService;
    }

    @Override
    public List<ArrayDesignRef> getArrayDesignList(String query) {
        return newArrayList(Iterables.transform(Iterables.limit(adList.getArrayDesigns(query), 20), new Function<AE.ArrayDesign, ArrayDesignRef>() {
            @Nullable
            @Override
            public ArrayDesignRef apply(@Nullable AE.ArrayDesign ad) {
                return new ArrayDesignRef(ad.getName(), ad.getDesription());
            }
        }));
    }

    @Override
    public List<EfoTermDto> getEfoTerms(String query, int limit) {
        return Collections.emptyList();
    }

    @Override
    public List<EfoTermDto> getEfoTermsFromBranch(String query, String branchId, int limit) {
        return Collections.emptyList();
    }
}
