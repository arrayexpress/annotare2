/*
 * Copyright 2009-2015 European Molecular Biology Laboratory
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

package uk.ac.ebi.fg.annotare2.web.gwt.common.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfileType;
import uk.ac.ebi.fg.annotare2.submission.model.OntologyTerm;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ApplicationProperties;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ArrayDesignRef;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.OntologyTermGroup;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.SystemEfoTermMap;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ProtocolType;

import java.util.ArrayList;

/**
 * @author Olga Melnichuk
 */
@RemoteServiceRelativePath(ApplicationDataService.NAME)
public interface ApplicationDataService extends RemoteService {

    String NAME = "appDataService";

    SystemEfoTermMap getSystemEfoTerms();

    ArrayList<ArrayDesignRef> getArrayDesignList(String query, int limit);

    ArrayList<OntologyTerm> getEfoTerms(String query, int limit);

    ArrayList<OntologyTerm> getEfoTerms(String query, String rootAccession, int limit);

    OntologyTerm getEfoTermByLabel(String label);

    ArrayList<ProtocolType> getProtocolTypes(ExperimentProfileType type);

    ArrayList<OntologyTerm> getContactRoles();

    ArrayList<OntologyTermGroup> getExperimentalDesigns();

    ApplicationProperties getApplicationProperties();

    ArrayList<String> getAeExperimentTypes(ExperimentProfileType type);

    ArrayList<String> getMaterialTypes();

    ArrayList<String> getSequencingHardware();

    ArrayList<String> getGeneratedNames(int numOfNames, String namingPattern, int startingNumber, boolean getOnlyFirstAndLast);
}
