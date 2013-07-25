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

package uk.ac.ebi.fg.annotare2.web.gwt.common.shared.update;

import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.*;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.columns.SampleColumn;

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

    void createSample();

    void removeSamples(List<SampleRow> row);

    void updateExtractAttributes(ExtractAttributesRow row);

    void updateExtractLabels(ExtractLabelsRow row);

    void createProtocol(ProtocolType protocolType);

    void updateProtocol(ProtocolRow row);

    void removeProtocols(List<ProtocolRow> rows);
}
