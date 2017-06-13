/*
 * Copyright 2009-2016 European Molecular Biology Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.web.gwt.common.shared.update;

import uk.ac.ebi.fg.annotare2.submission.model.ExtractAttribute;
import uk.ac.ebi.fg.annotare2.submission.model.FileType;
import uk.ac.ebi.fg.annotare2.submission.model.Protocol;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ExperimentSettings;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.*;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.columns.SampleColumn;

import java.util.HashMap;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public interface ExperimentUpdatePerformer {

    void createContact();

    void updateContact(ContactDto contact);

    void removeContacts(List<ContactDto> contacts);

    void createPublication();

    void updatePublication(PublicationDto publication);

    void removePublications(List<PublicationDto> publications);

    void updateDetails(ExperimentDetailsDto details);

    void updateSampleAttributes(List<SampleColumn> columns);

    void updateSample(SampleRow row);

    void createSamples(int numOfSamples, String namingPattern, int startingNumber);

    void removeSamples(List<SampleRow> row);

    void updateExtractAttributes(ExtractAttributesRow row);

    void updateExtractAttributes(HashMap<ExtractAttribute,String> values, int noOfSamples);

    void updateExtractLabels(LabeledExtractsRow row);

    void createProtocol(ProtocolType protocolType);

    void createProtocol(List<Protocol> protocols);

    void updateProtocol(ProtocolRow row);

    void removeProtocols(List<ProtocolRow> rows);

    void createDataAssignmentColumn(FileType fileType);

    void removeDataAssignmentColumns(List<Integer> indices);

    void updateDataAssignmentColumn(DataAssignmentColumn column);

    void updateProtocolAssignments(ProtocolAssignmentProfileUpdates updates);

    void moveProtocolDown(ProtocolRow row);

    void moveProtocolUp(ProtocolRow row);

    void updateSettings(ExperimentSettings settings);
}
