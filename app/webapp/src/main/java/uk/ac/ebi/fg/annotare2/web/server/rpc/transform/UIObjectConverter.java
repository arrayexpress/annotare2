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

package uk.ac.ebi.fg.annotare2.web.server.rpc.transform;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import uk.ac.ebi.fg.annotare2.db.model.*;
import uk.ac.ebi.fg.annotare2.db.model.enums.Role;
import uk.ac.ebi.fg.annotare2.magetabcheck.efo.EfoTerm;
import uk.ac.ebi.fg.annotare2.submission.model.ArrayDesignHeader;
import uk.ac.ebi.fg.annotare2.submission.model.OntologyTerm;
import uk.ac.ebi.fg.annotare2.submission.model.PrintingProtocol;
import uk.ac.ebi.fg.annotare2.submission.transform.DataSerializationException;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.SubmissionDetails;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.SubmissionRow;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.SubmissionType;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.arraydesign.ArrayDesignDetailsDto;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.arraydesign.PrintingProtocolDto;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.dto.UserDto;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.DataFileRow;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Collections2.transform;

/**
 * @author Olga Melnichuk
 */
public class UIObjectConverter {

    private static Function<User, UserDto> USER_TRANSFORM = new Function<User, UserDto>() {
        public UserDto apply(@Nullable User user) {
            checkNotNull(user);
            return new UserDto(user.getName(), isCurator(user), user.getReferrer()!=null && user.getReferrer()!="");
        }
    };

    private static Function<Submission, SubmissionRow> SUBMISSION_ROW = new Function<Submission, SubmissionRow>() {
        public SubmissionRow apply(@Nullable Submission submission) {
            checkNotNull(submission);
            return new SubmissionRow(
                    submission.getId(),
                    submission.getAccession(),
                    submission.getTitle(),
                    submission.getCreated(),
                    submission.getStatus(),
                    SUBMISSION_TYPE.apply(submission),
                    submission.getCreatedBy().getEmail()
            );
        }
    };

    private static Function<Submission, SubmissionDetails> SUBMISSION_DETAILS = new Function<Submission, SubmissionDetails>() {
        public SubmissionDetails apply(@Nullable Submission submission) {
            checkNotNull(submission);
            return new SubmissionDetails(
                    submission.getId(),
                    submission.getAccession(),
                    submission.getTitle(),
                    submission.getCreated(),
                    submission.getUpdated(),
                    submission.getStatus(),
                    SUBMISSION_TYPE.apply(submission),
                    submission.getFtpSubDirectory(),
                    submission.hasNoData(),
                    submission.getOwnedBy().equals(submission.getCreatedBy())
            );
        }
    };

    private static Function<Submission, SubmissionType> SUBMISSION_TYPE = new Function<Submission, SubmissionType>() {
        public SubmissionType apply(@Nullable Submission submission) {
            checkNotNull(submission);
            if (submission instanceof ExperimentSubmission)
                return SubmissionType.EXPERIMENT;
            else if (submission instanceof ArrayDesignSubmission)
                return SubmissionType.ARRAY_DESIGN;
            else if (submission instanceof ImportedExperimentSubmission)
                return SubmissionType.IMPORTED_EXPERIMENT;
            else
                throw new IllegalStateException("Submission is of unknown type: " + submission.getClass());
        }
    };

    private static Function<DataFile, DataFileRow> DATA_FILE_ROW = new Function<DataFile, DataFileRow>() {
        @Nullable
        @Override
        public DataFileRow apply(@Nullable DataFile dataFile) {
            checkNotNull(dataFile);
            return new DataFileRow(
                    dataFile.getId(),
                    dataFile.getName(),
                    dataFile.getDigest(),
                    dataFile.getStatus(),
                    dataFile.getCreated(),
                    dataFile.getFileSize()
            );
        }
    };

    private static boolean isCurator(User user) {
        Collection<UserRole> userRoles = user.getRoles();
        if (null != userRoles) {
            for (UserRole role : userRoles) {
                if (Role.CURATOR == role.getRole()) {
                    return true;
                }
            }
        }
        return false;
    }

    public static ArrayList<SubmissionRow> uiSubmissionRows(Collection<Submission> submissions) {
        return new ArrayList<>(filter(
                transform(submissions, SUBMISSION_ROW), Predicates.notNull()));
    }

    public static SubmissionDetails uiSubmissionDetails(Submission submission) {
        return SUBMISSION_DETAILS.apply(submission);
    }

    public static UserDto uiUser(User user) {
        return USER_TRANSFORM.apply(user);
    }

    public static ArrayList<OntologyTerm> uiEfoTerms(Collection<EfoTerm> terms) {
        return new ArrayList<>(transform(terms, new Function<EfoTerm, OntologyTerm>() {
            @Nullable
            @Override
            public OntologyTerm apply(@Nullable EfoTerm input) {
                return uiEfoTerm(input);
            }
        }));
    }

    public static OntologyTerm uiEfoTerm(EfoTerm term) {
        return term == null ? null : new OntologyTerm(term.getAccession(), term.getLabel());
    }

    public static PrintingProtocolDto uiPrintingProtocol(PrintingProtocol protocol) {
        return new PrintingProtocolDto(protocol.getId(), protocol.getName(), protocol.getDescription());
    }

    public static ArrayDesignDetailsDto uiArrayDesignDetails(ArrayDesignSubmission submission) throws DataSerializationException {
        ArrayDesignHeader header = submission.getHeader();
        return header == null ? null : new ArrayDesignDetailsDto(
                header.getName(),
                header.getDescription(),
                header.getVersion(),
                header.getOrganism(),
                header.getPublicReleaseDate(),
                uiPrintingProtocol(header.getPrintingProtocol())
        );
    }

    public static ArrayList<DataFileRow> uiDataFileRows(Collection<DataFile> files) {
        return new ArrayList<>(transform(files, DATA_FILE_ROW));
    }
}
