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

import com.google.common.io.Files;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.IDF;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.MAGETABInvestigation;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.SDRF;
import uk.ac.ebi.arrayexpress2.magetab.exception.ParseException;
import uk.ac.ebi.arrayexpress2.magetab.parser.IDFParser;
import uk.ac.ebi.arrayexpress2.magetab.parser.MAGETABParser;
import uk.ac.ebi.arrayexpress2.magetab.renderer.IDFWriter;
import uk.ac.ebi.arrayexpress2.magetab.renderer.SDRFWriter;
import uk.ac.ebi.arrayexpress2.magetab.renderer.adaptor.NodeFactory;
import uk.ac.ebi.fg.annotare2.configmodel.ExperimentProfile;
import uk.ac.ebi.fg.annotare2.magetab.integration.MageTabGenerator;
import uk.ac.ebi.fg.annotare2.web.server.UnexpectedException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import static com.google.common.io.Closeables.close;

/**
 * @author Olga Melnichuk
 */
public class MageTabFormat {

    private static final String DEFAULT_IDF_FILE_NAME = "idf.csv";
    private static final String DEFAULT_SDRF_FILE_NAME = "sdrf.csv";

    private File idfFile;
    private File sdrfFile;

    private IDF idf;
    private SDRF sdrf;

    public MageTabFormat() {
        File tmp = Files.createTempDir();
        idfFile = new File(tmp, DEFAULT_IDF_FILE_NAME);
        sdrfFile = new File(tmp, DEFAULT_SDRF_FILE_NAME);
    }

    private MageTabFormat(File directory, String idfFileName, String sdrfFileName) {
        this.idfFile = new File(directory, idfFileName);
        this.sdrfFile = new File(directory, sdrfFileName);
    }

    private void init(ExperimentProfile exp) throws IOException, ParseException {
        MAGETABInvestigation generated = (new MageTabGenerator(exp)).generate();

        /* Generated MAGE-TAB lacks cell locations, which are good to have during validation.
         * So we have to write files to disk and parse again */

        useDirtyHack();

        generated.IDF.sdrfFile.add(sdrfFile.getName());

        IDFWriter idfWriter = null;
        try {
            idfWriter = new IDFWriter(new FileWriter(idfFile));
            idfWriter.write(generated.IDF);
        } finally {
            close(idfWriter, true);
        }

        if (generated.SDRF.getRootNodes().isEmpty()) {
            /* Limpopo MAGE-TAB parser has a bug in reading and writing empty files. We have to create an empty file and
             * an empty SDRF as workaround */
            sdrfFile.createNewFile();

            idf = new IDFParser().parse(idfFile);
            sdrf = new SDRF();
        } else {
            SDRFWriter sdrfWriter = null;
            try {
                sdrfWriter = new SDRFWriter(new FileWriter(sdrfFile));
                sdrfWriter.write(generated.SDRF);
            } finally {
                close(sdrfWriter, true);
            }

            MAGETABParser parser = new MAGETABParser();
            MAGETABInvestigation inv = parser.parse(idfFile);
            idf = inv.IDF;
            sdrf = inv.SDRF;
        }
    }

    public IDF getIdf() {
        return idf;
    }

    public SDRF getSdrf() {
        return sdrf;
    }

    public File getIdfFile() {
        return idfFile;
    }

    public File getSdrfFile() {
        return sdrfFile;
    }

    public static MageTabFormat createMageTab(ExperimentProfile exp) throws IOException, ParseException {
        MageTabFormat format = new MageTabFormat();
        format.init(exp);
        return format;
    }

    public static MageTabFormat exportMageTab(ExperimentProfile exp, File exportDirectory,
                                        String idfFileName, String sdrfFileName) throws IOException, ParseException {
        MageTabFormat format = new MageTabFormat(exportDirectory, idfFileName, sdrfFileName);
        format.init(exp);
        return format;
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
            throw new UnexpectedException("MAGE TAB hack doesn't work", e);
        } catch (NoSuchMethodException e) {
            throw new UnexpectedException("MAGE TAB hack doesn't work", e);
        } catch (IllegalAccessException e) {
            throw new UnexpectedException("MAGE TAB hack doesn't work", e);
        } catch (InvocationTargetException e) {
            throw new UnexpectedException("MAGE TAB hack doesn't work", e);
        } catch (InstantiationException e) {
            throw new UnexpectedException("MAGE TAB hack doesn't work", e);
        } catch (NoSuchFieldException e) {
            throw new UnexpectedException("MAGE TAB hack doesn't work", e);
        }
    }

    private static NodeFactory newNodeFactoryHack() throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class<?> clazz = Class.forName("uk.ac.ebi.arrayexpress2.magetab.renderer.adaptor.NodeFactory");
        Constructor<?> constructor = clazz.getDeclaredConstructor();
        constructor.setAccessible(true);
        return (NodeFactory) constructor.newInstance();
    }

}
