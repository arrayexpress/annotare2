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

package uk.ac.ebi.fg.annotare2.web.server.rpc;

import com.google.gwt.user.server.rpc.UnexpectedException;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.IDF;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.MAGETABInvestigation;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.SDRF;
import uk.ac.ebi.arrayexpress2.magetab.exception.ParseException;
import uk.ac.ebi.arrayexpress2.magetab.renderer.IDFWriter;
import uk.ac.ebi.arrayexpress2.magetab.renderer.SDRFWriter;
import uk.ac.ebi.arrayexpress2.magetab.renderer.adaptor.NodeFactory;
import uk.ac.ebi.fg.annotare2.configmodel.ArrayDesignHeader;
import uk.ac.ebi.fg.annotare2.configmodel.ExperimentProfile;
import uk.ac.ebi.fg.annotare2.dao.RecordNotFoundException;
import uk.ac.ebi.fg.annotare2.magetab.integration.MageTabGenerator;
import uk.ac.ebi.fg.annotare2.magetab.table.Table;
import uk.ac.ebi.fg.annotare2.magetab.table.TsvParser;
import uk.ac.ebi.fg.annotare2.om.ArrayDesignSubmission;
import uk.ac.ebi.fg.annotare2.om.ExperimentSubmission;
import uk.ac.ebi.fg.annotare2.om.Submission;
import uk.ac.ebi.fg.annotare2.om.enums.Permission;
import uk.ac.ebi.fg.annotare2.configmodel.DataSerializationException;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.NoPermissionException;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.ResourceNotFoundException;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.SubmissionService;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ExperimentSettings;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.SubmissionDetails;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.arraydesign.ArrayDesignDetailsDto;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.*;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.update.*;
import uk.ac.ebi.fg.annotare2.web.server.login.AuthService;
import uk.ac.ebi.fg.annotare2.web.server.rpc.transform.UIObjectConverter;
import uk.ac.ebi.fg.annotare2.web.server.services.AccessControlException;
import uk.ac.ebi.fg.annotare2.web.server.services.SubmissionManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.List;

import static com.google.common.io.Closeables.close;
import static uk.ac.ebi.fg.annotare2.web.server.rpc.ExperimentUpdater.experimentUpdater;
import static uk.ac.ebi.fg.annotare2.web.server.rpc.transform.ExperimentBuilderFactory.createExperiment;

/**
 * @author Olga Melnichuk
 */
public class SubmissionServiceImpl extends AuthBasedRemoteService implements SubmissionService {

    private static final Logger log = LoggerFactory.getLogger(SubmissionServiceImpl.class);

    private final SubmissionManager submissionManager;

    @Inject
    public SubmissionServiceImpl(AuthService authService, SubmissionManager submissionManager) {
        super(authService);
        this.submissionManager = submissionManager;
    }

    @Override
    public SubmissionDetails getSubmission(int id) throws ResourceNotFoundException, NoPermissionException {
        try {
            Submission sb = submissionManager.getSubmission(getCurrentUser(), id, Permission.VIEW);
            return UIObjectConverter.uiSubmissionDetails(sb);
        } catch (AccessControlException e) {
            throw noPermission(e, Permission.VIEW);
        } catch (RecordNotFoundException e) {
            throw noSuchRecord(e);
        }
    }


    @Override
    public ArrayDesignDetailsDto getArrayDesignDetails(int id) throws ResourceNotFoundException, NoPermissionException {
        try {
            ArrayDesignSubmission sb = submissionManager.getArrayDesignSubmission(getCurrentUser(), id, Permission.VIEW);
            return UIObjectConverter.uiArrayDesignDetails(sb);
        } catch (AccessControlException e) {
            throw noPermission(e, Permission.VIEW);
        } catch (RecordNotFoundException e) {
            throw noSuchRecord(e);
        } catch (DataSerializationException e) {
            throw unexpected(e);
        }
    }

    @Override
    public Table getIdfTable(int id) throws NoPermissionException, ResourceNotFoundException {
        try {
            ExperimentSubmission submission = submissionManager.getExperimentSubmission(getCurrentUser(), id, Permission.VIEW);
            ExperimentProfile exp = submission.getExperimentProfile();
            MAGETABInvestigation inv = new MageTabGenerator(exp).generate();
            return asTable(inv.IDF);
        } catch (AccessControlException e) {
            throw noPermission(e, Permission.VIEW);
        } catch (RecordNotFoundException e) {
            throw noSuchRecord(e);
        } catch (IOException e) {
            throw unexpected(e);
        } catch (DataSerializationException e) {
            throw unexpected(e);
        } catch (ParseException e) {
            throw unexpected(e);
        }
    }

    @Override
    public Table getSdrfTable(int id) throws NoPermissionException, ResourceNotFoundException {
        try {
            ExperimentSubmission submission = submissionManager.getExperimentSubmission(getCurrentUser(), id, Permission.VIEW);
            ExperimentProfile exp = submission.getExperimentProfile();
            MAGETABInvestigation inv = (new MageTabGenerator(exp)).generate();
            return asTable(inv.SDRF);
        } catch (AccessControlException e) {
            throw noPermission(e, Permission.VIEW);
        } catch (RecordNotFoundException e) {
            throw noSuchRecord(e);
        } catch (IOException e) {
            throw unexpected(e);
        } catch (DataSerializationException e) {
            throw unexpected(e);
        } catch (ParseException e) {
            throw unexpected(e);
        }
    }

    private Table asTable(IDF idf) throws IOException {
        useDirtyHack();

        File tmpFile = File.createTempFile("idf", "tmp");
        IDFWriter writer = null;
        try {
            writer = new IDFWriter(new FileWriter(tmpFile));
            writer.write(idf);
            return new TsvParser().parse(new FileInputStream(tmpFile));
        } finally {
            close(writer, true);
            //TODO delete temporary file ?
        }
    }

    private Table asTable(SDRF sdrf) throws IOException {
        useDirtyHack();

        if (sdrf.getRootNodes().isEmpty()) {
            /* A workaround for SDRFWriter bug: an IndexOutOfBoundException is thrown by SDRFWriter,
            when you try to write en empty SDRF content */
            return new Table();
        }

        File tmpFile = File.createTempFile("sdrf", "tmp");
        SDRFWriter writer = null;
        try {
            writer = new SDRFWriter(new FileWriter(tmpFile));
            writer.write(sdrf);
            return new TsvParser().parse(new FileInputStream(tmpFile));
        } finally {
            close(writer, true);
            //TODO delete temporary file ?
        }
    }

    @Override
    public int createExperimentSubmission() throws NoPermissionException {
        try {
            return submissionManager.createExperimentSubmission(getCurrentUser()).getId();
        } catch (AccessControlException e) {
            throw noPermission(e, Permission.CREATE);
        }
    }

    @Override
    public int createArrayDesignSubmission() throws NoPermissionException {
        try {
            return submissionManager.createArrayDesignSubmission(getCurrentUser()).getId();
        } catch (AccessControlException e) {
            throw noPermission(e, Permission.CREATE);
        }
    }

    @Override
    public void setupExperimentSubmission(int id, ExperimentSetupSettings settings) throws ResourceNotFoundException, NoPermissionException {
        try {
            ExperimentSubmission submission =
                    submissionManager.getExperimentSubmission(getCurrentUser(), id, Permission.UPDATE);
            submission.setExperimentProfile(createExperiment(settings));
        } catch (RecordNotFoundException e) {
            throw noSuchRecord(e);
        } catch (AccessControlException e) {
            throw noPermission(e, Permission.UPDATE);
        } catch (DataSerializationException e) {
            throw unexpected(e);
        }
    }

    @Override
    public void discardSubmissionData(int id) throws ResourceNotFoundException, NoPermissionException {
        try {
            Submission submission =
                    submissionManager.getSubmission(getCurrentUser(), id, Permission.UPDATE);
            submission.discardAll();
        } catch (RecordNotFoundException e) {
            throw noSuchRecord(e);
        } catch (AccessControlException e) {
            throw noPermission(e, Permission.UPDATE);
        }
    }

    @Override
    public ExperimentProfile updateExperiment(int id, List<ExperimentUpdateCommand> commands) throws ResourceNotFoundException, NoPermissionException {
        try {
            ExperimentSubmission submission = submissionManager.getExperimentSubmission(getCurrentUser(), id, Permission.UPDATE);
            ExperimentProfile experiment = submission.getExperimentProfile();
            experimentUpdater(experiment).run(commands);
            submission.setExperimentProfile(experiment);
            return experiment;
        } catch (RecordNotFoundException e) {
            throw noSuchRecord(e);
        } catch (AccessControlException e) {
            throw noPermission(e, Permission.UPDATE);
        } catch (DataSerializationException e) {
            throw unexpected(e);
        }
    }

    @Override
    public ArrayDesignUpdateResult updateArrayDesign(int id, List<ArrayDesignUpdateCommand> commands) throws ResourceNotFoundException, NoPermissionException {
        try {
            ArrayDesignSubmission submission = submissionManager.getArrayDesignSubmission(getCurrentUser(), id, Permission.UPDATE);
            ArrayDesignHeader header = submission.getHeader();
            ArrayDesignUpdateResult result = new ArrayDesignUpdatePerformerImpl(header).run(commands);
            submission.setHeader(header);
            return result;
        } catch (RecordNotFoundException e) {
            throw noSuchRecord(e);
        } catch (AccessControlException e) {
            throw noPermission(e, Permission.UPDATE);
        } catch (DataSerializationException e) {
            throw unexpected(e);
        }
    }

    @Override
    public ExperimentProfile loadExperiment(int id) throws ResourceNotFoundException, NoPermissionException {
        try {
            ExperimentSubmission submission = submissionManager.getExperimentSubmission(getCurrentUser(), id, Permission.VIEW);
            return submission.getExperimentProfile();
        } catch (RecordNotFoundException e) {
            throw noSuchRecord(e);
        } catch (AccessControlException e) {
            throw noPermission(e, Permission.UPDATE);
        } catch (DataSerializationException e) {
            throw unexpected(e);
        }
    }

    /**
     * A workaround to reset NodeFactory.instance field to reflect changes in SDRF nodes;
     * without this workaround SDRFWriter uses SDRF nodes from the first run;
     */
    private static void useDirtyHack() {
        try {
            NodeFactory newValue = newNodeFactoryHack();

            Field field = NodeFactory.class.getDeclaredField("instance");
            field.setAccessible(true);
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

            field.set(null, newValue);
        } catch (ClassNotFoundException e) {
            throw unexpected(e);
        } catch (NoSuchMethodException e) {
            throw unexpected(e);
        } catch (IllegalAccessException e) {
            throw unexpected(e);
        } catch (InvocationTargetException e) {
            throw unexpected(e);
        } catch (InstantiationException e) {
            throw unexpected(e);
        } catch (NoSuchFieldException e) {
            throw unexpected(e);
        }
    }

    private static NodeFactory newNodeFactoryHack() throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class<?> clazz = Class.forName("uk.ac.ebi.arrayexpress2.magetab.renderer.adaptor.NodeFactory");
        Constructor<?> constructor = clazz.getDeclaredConstructor();
        constructor.setAccessible(true);
        return (NodeFactory) constructor.newInstance();
    }

    private static UnexpectedException unexpected(Exception e) {
        log.error("server error", e);
        return new UnexpectedException("Unexpected server error", e);
    }

    private static ResourceNotFoundException noSuchRecord(RecordNotFoundException e) {
        log.error("server error", e);
        return new ResourceNotFoundException("Submission not found");
    }

    private static NoPermissionException noPermission(AccessControlException e, Permission permission) {
        log.error("server error", e);
        return new NoPermissionException("Sorry you do not have permission to '" + permission + "' the submission");
    }
}
