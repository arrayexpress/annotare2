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

import com.google.inject.Inject;
import org.apache.commons.fileupload.FileItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fg.annotare2.db.dao.RecordNotFoundException;
import uk.ac.ebi.fg.annotare2.db.model.ArrayDesignSubmission;
import uk.ac.ebi.fg.annotare2.db.model.enums.Permission;
import uk.ac.ebi.fg.annotare2.submission.model.ArrayDesignHeader;
import uk.ac.ebi.fg.annotare2.submission.model.PrintingProtocol;
import uk.ac.ebi.fg.annotare2.submission.transform.DataSerializationException;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.AdfService;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.DataImportException;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.NoPermissionException;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.ResourceNotFoundException;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.table.Table;
import uk.ac.ebi.fg.annotare2.web.server.magetab.adf.AdfHeader;
import uk.ac.ebi.fg.annotare2.web.server.magetab.adf.AdfParser;
import uk.ac.ebi.fg.annotare2.web.server.magetab.tsv.TsvGenerator;
import uk.ac.ebi.fg.annotare2.web.server.magetab.tsv.TsvParser;
import uk.ac.ebi.fg.annotare2.web.server.services.*;
import uk.ac.ebi.fg.annotare2.web.server.transaction.Transactional;

import java.io.IOException;

/**
 * @author Olga Melnichuk
 */
public class AdfServiceImpl extends SubmissionBasedRemoteService implements AdfService {

    private static final Logger log = LoggerFactory.getLogger(AdfServiceImpl.class);

    @Inject
    public AdfServiceImpl(AccountService accountService, SubmissionManager submissionManager, EmailSender emailSender) {
        super(accountService, submissionManager, emailSender);
    }

    @Transactional
    @Override
    public Table loadBodyData(int submissionId) throws NoPermissionException, ResourceNotFoundException {
        try {
            ArrayDesignSubmission submission = getArrayDesignSubmission(submissionId, Permission.VIEW);
            return new TsvParser().parse(submission.getBody());
        } catch (IOException e) {
            throw unexpected(e);
        } catch (AccessControlException e) {
            throw noPermission(e);
        } catch (RecordNotFoundException e) {
            throw noSuchRecord(e);
        }
    }

    @Transactional(rollbackOn = {NoPermissionException.class, ResourceNotFoundException.class, DataImportException.class})
    @Override
    public void importBodyData(int submissionId) throws NoPermissionException, ResourceNotFoundException, DataImportException {
        try {
            final ArrayDesignSubmission submission = getArrayDesignSubmission(submissionId, Permission.UPDATE);

            FileItem item = UploadedFiles.getFirst(getSession());
            Table bodyTable = new AdfParser().parseBody(item.getInputStream());
            final String body = bodyTable.isEmpty() ? "" : new TsvGenerator(bodyTable).generateString();

            Table headerTable = new AdfParser().parseHeader(item.getInputStream());
            final ArrayDesignHeader header = createArrayDesignHeader(headerTable);

            submission.setBody(body);
            submission.setHeader(header);
            save(submission);
        } catch (IOException e) {
            log.warn("Unable to import ADF body data (submissionId: " + submissionId + ")", e);
            throw new DataImportException(e.getMessage());
        } catch (AccessControlException e) {
            throw noPermission(e);
        } catch (RecordNotFoundException e) {
            throw noSuchRecord(e);
        } catch (DataSerializationException e) {
            throw unexpected(e);
        }
    }

    private ArrayDesignHeader createArrayDesignHeader(Table table) {
        ArrayDesignHeader adHeader = new ArrayDesignHeader();
        if (table.isEmpty()) {
            return adHeader;
        }

        AdfHeader header = new AdfHeader(table);
        adHeader.setName(header.getArrayDesignName());
        adHeader.setDescription(header.getDescription());
        adHeader.setVersion(header.getVersion());
        adHeader.setPrintingProtocol(PrintingProtocol.parse(header.getPrintingProtocol()));
        //TODO needed Organism lookup service adHeader.setOrganism(header.getOrganism(false));
        return adHeader;
    }
}
