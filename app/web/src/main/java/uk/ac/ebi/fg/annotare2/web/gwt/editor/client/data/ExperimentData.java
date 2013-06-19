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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.data;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import uk.ac.ebi.fg.annotare2.configmodel.*;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.AsyncCallbackWrapper;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.SubmissionServiceAsync;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ExperimentSettings;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.dto.EfoTermDto;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.*;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.columns.*;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.update.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static uk.ac.ebi.fg.annotare2.web.gwt.editor.client.EditorUtils.getSubmissionId;

/**
 * @author Olga Melnichuk
 */
public class ExperimentData {

    private final SubmissionServiceAsync submissionService;

    private final UpdateQueue<ExperimentUpdateCommand, ExperimentProfile> updateQueue;

    private ExperimentProfile exp;

    @Inject
    public ExperimentData(EventBus eventBus,
                          SubmissionServiceAsync submissionServiceAsync) {
        submissionService = submissionServiceAsync;

        updateQueue =
                new UpdateQueue<ExperimentUpdateCommand, ExperimentProfile>(eventBus,
                        new UpdateQueue.Transport<ExperimentUpdateCommand, ExperimentProfile>() {
                            @Override
                            public void sendUpdates(List<ExperimentUpdateCommand> commands, final AsyncCallback<ExperimentProfile> callback) {
                                submissionService.updateExperiment(getSubmissionId(), commands, new AsyncCallbackWrapper<ExperimentProfile>() {
                                    @Override
                                    public void onFailure(Throwable caught) {
                                        callback.onFailure(caught);
                                    }

                                    @Override
                                    public void onSuccess(ExperimentProfile result) {
                                        exp = result;
                                        callback.onSuccess(result);
                                    }
                                }.wrap());
                            }
                        });

        GWT.log(getClass().getName() + ": initialized");
    }

    private void getExperiment(final AsyncCallback<ExperimentProfile> callback) {
        if (exp != null) {
            callback.onSuccess(exp);
            return;
        }
        submissionService.loadExperiment(getSubmissionId(), new AsyncCallbackWrapper<ExperimentProfile>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(ExperimentProfile result) {
                exp = result;
                callback.onSuccess(exp);
            }
        }.wrap());
    }

    private ExperimentDetailsDto getExperimentDetails(ExperimentProfile exp) {
        return new ExperimentDetailsDto(
                exp.getTitle(),
                exp.getDescription(),
                exp.getExperimentDate(),
                exp.getPublicReleaseDate()
        );
    }

    private List<ContactDto> getContacts(ExperimentProfile exp) {
        List<ContactDto> contacts = new ArrayList<ContactDto>();
        for (Contact contact : exp.getContacts()) {
            contacts.add(new ContactDto(
                    contact.getId(),
                    contact.getId(),
                    contact.getFirstName(),
                    contact.getLastName(),
                    contact.getMidInitials(),
                    contact.getEmail(),
                    contact.getPhone(),
                    contact.getFax(),
                    contact.getAffiliation(),
                    contact.getAddress(),
                    contact.getRoles()
            ));
        }
        return contacts;
    }

    private List<PublicationDto> getPublications(ExperimentProfile exp) {
        List<PublicationDto> publications = new ArrayList<PublicationDto>();
        for (Publication publication : exp.getPublications()) {
            publications.add(new PublicationDto(
                    publication.getId(),
                    publication.getId(),
                    publication.getTitle(),
                    publication.getAuthors(),
                    publication.getPubMedId()
            ));
        }
        return publications;
    }

    private SampleRowsAndColumns getSampleRowsAndColumns(ExperimentProfile exp) {
        return new SampleRowsAndColumns(
                getSampleRows(exp),
                getSampleColumns(exp));
    }

    private List<SampleRow> getSampleRows(ExperimentProfile exp) {
        List<SampleRow> rows = new ArrayList<SampleRow>();
        for (Sample sample : exp.getSamples()) {
            rows.add(new SampleRow(
                    sample.getId(),
                    sample.getName(),
                    sample.getValues()
            ));
        }
        return rows;
    }

    private List<SampleColumn> getSampleColumns(ExperimentProfile exp) {
        List<SampleColumn> columns = new ArrayList<SampleColumn>();
        for (SampleAttribute attr : exp.getSampleAttributes()) {
            AttributeValueTypeVisitor visitor = new AttributeValueTypeVisitor();
            attr.getValueType().visit(visitor);
            columns.add(new SampleColumn(
                    attr.getId(),
                    attr.getName(),
                    attr.getType(),
                    visitor.getValueType(),
                    attr.isEditable()
            ));
        }
        return columns;
    }

    private List<ExtractAttributeRow> getExtractAttributeRows(ExperimentProfile exp) {
        List<ExtractAttributeRow> rows = new ArrayList<ExtractAttributeRow>();
        for(Extract extract : exp.getExtracts()) {
            rows.add(new ExtractAttributeRow(extract.getId(), extract.getName(), extract.getValues()));
        }
        return rows;
    }

    private List<ExtractLabelsRow> getExtractLabelsRows(ExperimentProfile exp) {
        Map<Integer, ExtractLabelsRow> map = new LinkedHashMap<Integer, ExtractLabelsRow>();
        for(LabeledExtract labeledExtract : exp.getLabeledExtracts()) {
            Extract extract = labeledExtract.getExtract();
            Integer extractId = extract.getId();
            ExtractLabelsRow row = map.get(extractId);
            if (row == null) {
                row = new ExtractLabelsRow(extractId, extract.getName());
                map.put(extractId, row);
            }
            row.addLabel(labeledExtract.getLabel());
        }
        return new ArrayList<ExtractLabelsRow>(map.values());
    }

    public void getSettingsAsync(final AsyncCallback<ExperimentSettings> callback) {
        getExperiment(new AsyncCallback<ExperimentProfile>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(ExperimentProfile result) {
                callback.onSuccess(new ExperimentSettings(result.getType()));
            }
        });
    }

    public void getDetailsAsync(final AsyncCallback<ExperimentDetailsDto> callback) {
        getExperiment(new AsyncCallback<ExperimentProfile>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(ExperimentProfile result) {
                callback.onSuccess(getExperimentDetails(result));
            }
        });
    }

    public void getContactsAsync(final AsyncCallback<List<ContactDto>> callback) {
        getExperiment(new AsyncCallback<ExperimentProfile>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(ExperimentProfile result) {
                callback.onSuccess(getContacts(result));
            }
        });
    }

    public void getPublicationsAsync(final AsyncCallback<List<PublicationDto>> callback) {
        getExperiment(new AsyncCallback<ExperimentProfile>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(ExperimentProfile result) {
                callback.onSuccess(getPublications(result));
            }
        });
    }

    public void getSamplesAsync(final AsyncCallback<SampleRowsAndColumns> callback) {
        getExperiment(new AsyncCallback<ExperimentProfile>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(ExperimentProfile result) {
                callback.onSuccess(getSampleRowsAndColumns(result));
            }
        });
    }

    public void getExtractAttributeRowsAsync(final AsyncCallback<List<ExtractAttributeRow>> callback) {
        getExperiment(new AsyncCallback<ExperimentProfile>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(ExperimentProfile result) {
                callback.onSuccess(getExtractAttributeRows(result));
            }
        });
    }

    public void getLabeledExtractsAsync(final AsyncCallback<LabeledExtracts> callback) {
        getExperiment(new AsyncCallback<ExperimentProfile>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(ExperimentProfile result) {
                callback.onSuccess(new LabeledExtracts(getExtractLabelsRows(result)));
            }
        });
    }

    public void createContact() {
        updateQueue.add(new CreateContactCommand());
    }

    public void updateContact(ContactDto toBeUpdated) {
        updateQueue.add(new UpdateContactCommand(toBeUpdated));
    }

    public void updateContacts(List<ContactDto> toBeUpdated) {
        for (ContactDto contact : toBeUpdated) {
            updateContact(contact);
        }
    }

    public void removeContacts(List<ContactDto> toBeRemoved) {
        updateQueue.add(new RemoveContactsCommand(toBeRemoved));
    }

    public void updateDetails(ExperimentDetailsDto toBeUpdated) {
        updateQueue.add(new UpdateExperimentDetailsCommand(toBeUpdated));
    }

    public void createPublication() {
        updateQueue.add(new CreatePublicationCommand());
    }

    public void updatePublication(PublicationDto toBeUpdated) {
        updateQueue.add(new UpdatePublicationCommand(toBeUpdated));
    }

    public void updatePublications(List<PublicationDto> toBeUpdated) {
        for (PublicationDto publication : toBeUpdated) {
            updatePublication(publication);
        }
    }

    public void removePublications(List<PublicationDto> toBeRemoved) {
        updateQueue.add(new RemovePublicationsCommand(toBeRemoved));
    }

    public void createSample() {
        updateQueue.add(new CreateSampleCommand());
    }

    public void updateSampleColumns(List<SampleColumn> columns) {
        updateQueue.add(new UpdateSampleColumnsCommand(columns));
    }

    public void updateSampleRow(SampleRow row) {
        updateQueue.add(new UpdateSampleRowCommand(row));
    }

    public void removeSamples(List<SampleRow> rows) {
        updateQueue.add(new RemoveSamplesCommand(rows));
    }

    private static class AttributeValueTypeVisitor implements AttributeValueType.Visitor {

        private ColumnValueType valueType;

        @Override
        public void visitNumericValueType(NumericAttributeValueType valueType) {
            OntologyTerm units = valueType.getUnits();
            this.valueType = new NumericValueType(new EfoTermDto(units.getAccession(), units.getLabel()));
        }

        @Override
        public void visitTextValueType(TextAttributeValueType valueType) {
            this.valueType = new TextValueType();
        }

        @Override
        public void visitTermValueType(TermAttributeValueType valueType) {
            OntologyTerm branch = valueType.getBranch();
            this.valueType = new EfoTermValueType(new EfoTermDto(branch.getAccession(), branch.getLabel()));
        }

        public ColumnValueType getValueType() {
            return valueType;
        }
    }
}
