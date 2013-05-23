/*
 * Copyright 2009-2012 European Molecular Biology Laboratory
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

package uk.ac.ebi.fg.annotare2.web.server.rpc.transform;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import uk.ac.ebi.fg.annotare2.configmodel.*;
import uk.ac.ebi.fg.annotare2.om.ArrayDesignSubmission;
import uk.ac.ebi.fg.annotare2.om.ExperimentSubmission;
import uk.ac.ebi.fg.annotare2.om.Submission;
import uk.ac.ebi.fg.annotare2.om.User;
import uk.ac.ebi.fg.annotare2.services.efo.EfoNode;
import uk.ac.ebi.fg.annotare2.submissionmodel.DataSerializationException;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ExperimentSettings;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.SubmissionDetails;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.SubmissionRow;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.SubmissionType;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.dto.EfoTermDto;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.dto.UserDto;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.*;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.columns.*;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Collections2.transform;
import static com.google.common.collect.Lists.transform;

/**
 * @author Olga Melnichuk
 */
public class UIObjectConverter {

    static Function<User, UserDto> USER_TRANSFORM = new Function<User, UserDto>() {
        public UserDto apply(@Nullable User user) {
            checkNotNull(user);
            return new UserDto(user.getEmail());
        }
    };

    static Function<Submission, SubmissionRow> SUBMISSION_ROW = new Function<Submission, SubmissionRow>() {
        public SubmissionRow apply(@Nullable Submission submission) {
            checkNotNull(submission);
            return new SubmissionRow(
                    submission.getId(),
                    submission.getAccession(),
                    submission.getTitle(),
                    submission.getCreated(),
                    submission.getStatus(),
                    SUBMISSION_TYPE.apply(submission)
            );
        }
    };

    static Function<Submission, SubmissionDetails> SUBMISSION_DETAILS = new Function<Submission, SubmissionDetails>() {
        public SubmissionDetails apply(@Nullable Submission submission) {
            checkNotNull(submission);
            return new SubmissionDetails(
                    submission.getId(),
                    submission.getAccession(),
                    submission.getTitle(),
                    submission.getCreated(),
                    submission.getStatus(),
                    SUBMISSION_TYPE.apply(submission),
                    submission.hasNoData()
            );
        }
    };

    static Function<Submission, SubmissionType> SUBMISSION_TYPE = new Function<Submission, SubmissionType>() {
        public SubmissionType apply(@Nullable Submission submission) {
            checkNotNull(submission);
            if (submission instanceof ExperimentSubmission)
                return SubmissionType.EXPERIMENT;
            else if (submission instanceof ArrayDesignSubmission)
                return SubmissionType.ARRAY_DESIGN;
            throw new IllegalStateException("Submission is of unknown type: " + submission.getClass());
        }
    };

    static Function<SampleProfile, SampleRow> SAMPLE_ROW = new Function<SampleProfile, SampleRow>() {
        @Nullable
        @Override
        public SampleRow apply(@Nullable SampleProfile sample) {
            checkNotNull(sample);
            return new SampleRow(sample.getId(), sample.getName(), sample.getValues());
        }
    };

    static Function<SampleAttribute, SampleColumn> SAMPLE_COLUMN = new Function<SampleAttribute, SampleColumn>() {
        @Nullable
        @Override
        public SampleColumn apply(@Nullable SampleAttribute input) {
            AttributeValueTypeVisitor visitor = new AttributeValueTypeVisitor();
            input.getValueType().visit(visitor);

            return new SampleColumn(
                    input.getName(),
                    input.getType(),
                    visitor.getValueType());
        }
    };

    static Function<Contact, ContactDto> CONTACT_DTO = new Function<Contact, ContactDto>() {
        @Nullable
        @Override
        public ContactDto apply(@Nullable Contact contact) {
            checkNotNull(contact);
            return new ContactDto(
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
                    contact.getRoles());
        }
    };

    static Function<Publication, PublicationDto> PUBLICATION_DTO = new Function<Publication, PublicationDto>() {
        @Nullable
        @Override
        public PublicationDto apply(@Nullable Publication publication) {
            return new PublicationDto(
                    publication.getId(),
                    publication.getId(),
                    publication.getTitle(),
                    publication.getAuthors(),
                    publication.getPubMedId()
            );
        }
    };

    public static ArrayList<SubmissionRow> uiSubmissionRows(List<Submission> submissions) {
        return new ArrayList<SubmissionRow>(filter(
                transform(submissions, SUBMISSION_ROW), Predicates.notNull()));
    }

    public static SubmissionDetails uiSubmissionDetails(Submission submission) {
        return SUBMISSION_DETAILS.apply(submission);
    }

    public static ExperimentSettings uiExperimentSubmissionSettings(ExperimentSubmission submission)
            throws DataSerializationException {
        ExperimentProfile exp = submission.getExperimentConfig();
        return new ExperimentSettings(exp.getType());
    }

    public static DetailsDto uiExperimentDetails(ExperimentSubmission submission) throws DataSerializationException {
        ExperimentProfile exp = submission.getExperimentConfig();
        return new DetailsDto(
                exp.getTitle(),
                exp.getDescription(),
                exp.getExperimentDate(),
                exp.getPublicReleaseDate()
        );
    }

    public static SampleRowsAndColumns uiSampleRowsAndColumns(ExperimentSubmission submission) throws DataSerializationException {
        ExperimentProfile exp = submission.getExperimentConfig();
        return new SampleRowsAndColumns(
                new ArrayList<SampleRow>(transform(exp.getSamples(), SAMPLE_ROW)),
                new ArrayList<SampleColumn>(transform(exp.getSampleAttributes(), SAMPLE_COLUMN)));
    }

    public static UserDto uiUser(User user) {
        return USER_TRANSFORM.apply(user);
    }

    public static List<ContactDto> uiContacts(ExperimentSubmission submission) throws DataSerializationException {
        ExperimentProfile exp = submission.getExperimentConfig();
        return uiContacts(exp.getContacts());
    }

    public static List<ContactDto> uiContacts(Collection<Contact> contacts) throws DataSerializationException {
        return new ArrayList<ContactDto>(transform(contacts, CONTACT_DTO));
    }

    public static List<PublicationDto> uiPublications(ExperimentSubmission submission) throws DataSerializationException {
        ExperimentProfile exp = submission.getExperimentConfig();
        return new ArrayList<PublicationDto>(transform(exp.getPublications(), PUBLICATION_DTO));
    }

    public static List<EfoTermDto> uiEfoTerms(Collection<EfoNode> terms) {
        return new ArrayList<EfoTermDto>(transform(terms, new Function<EfoNode, EfoTermDto>() {
            @Nullable
            @Override
            public EfoTermDto apply(@Nullable EfoNode input) {
                return uiEfoTerm(input);
            }
        }));
    }

    public static EfoTermDto uiEfoTerm(EfoNode term) {
        return term == null ? null : new EfoTermDto(term.getAccession(), term.getName());
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
