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

package uk.ac.ebi.fg.annotare2.web.server.magetab;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.IDF;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.MAGETABInvestigation;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.SDRF;
import uk.ac.ebi.arrayexpress2.magetab.exception.ParseException;
import uk.ac.ebi.arrayexpress2.magetab.parser.IDFParser;
import uk.ac.ebi.arrayexpress2.magetab.parser.MAGETABParser;
import uk.ac.ebi.arrayexpress2.magetab.renderer.IDFWriter;
import uk.ac.ebi.arrayexpress2.magetab.renderer.SDRFWriter;
import uk.ac.ebi.arrayexpress2.magetab.renderer.adaptor.NodeFactory;
import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfile;
import uk.ac.ebi.fg.annotare2.web.server.UnexpectedException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import static com.google.common.io.Closeables.close;
import static uk.ac.ebi.fg.annotare2.web.server.magetab.MageTabGenerator.replaceAllAssayNameValues;
import static uk.ac.ebi.fg.annotare2.web.server.magetab.MageTabGenerator.replaceAllUnassignedValues;

/**
 * @author Olga Melnichuk
 */
public class MageTabFiles {

    private static final Logger log = LoggerFactory.getLogger(MageTabFiles.class);

    private final File idfFile;
    private final File sdrfFile;

    private IDF idf;
    private SDRF sdrf;

    private boolean sanitize;

    private MageTabFiles(File idfFile, File sdrfFile) {
        this(idfFile, sdrfFile, true);
    }

    private MageTabFiles(File idfFile, File sdrfFile, boolean sanitize) {
        this.idfFile = idfFile;
        this.sdrfFile = sdrfFile;
        this.sanitize = sanitize;
    }

    private MageTabFiles init(ExperimentProfile exp) throws IOException, ParseException {
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

            sanitize(sdrfFile, sanitize);
        }
        return this;
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

    public static MageTabFiles createMageTabFiles(ExperimentProfile exp, boolean sanitize) throws IOException, ParseException {
        File tmp = Files.createTempDir();
        tmp.deleteOnExit();
        return (new MageTabFiles(new File(tmp, "idf.csv"), new File(tmp, "sdrf.csv"), sanitize)).init(exp);
    }

    public static MageTabFiles createMageTabFiles(ExperimentProfile exp, File directory, String idfFileName,
                                                  String sdrfFileName) throws IOException, ParseException {
        return (new MageTabFiles(new File(directory, idfFileName), new File(directory, sdrfFileName))).init(exp);
    }

    /**
     * Substitutes fake values (which were required to build MAGE-TAB graph) to a required values.
     *
     * @param file file to be sanitized
     */
    private static void sanitize(File file, boolean everything) {
        try {
            String str = Files.toString(file, Charsets.UTF_8);
            str = replaceAllAssayNameValues(str);

            if (everything) {
                str = replaceAllUnassignedValues(str);
            }
            Files.write(str, file, Charsets.UTF_8);
        } catch (IOException e) {
            log.error("Can't sanitize MAGE-TAB file" + file.getAbsolutePath(), e);
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
            throw unexpectedException(e);
        } catch (NoSuchMethodException e) {
            throw unexpectedException(e);
        } catch (IllegalAccessException e) {
            throw unexpectedException(e);
        } catch (InvocationTargetException e) {
            throw unexpectedException(e);
        } catch (InstantiationException e) {
            throw unexpectedException(e);
        } catch (NoSuchFieldException e) {
            throw unexpectedException(e);
        }
    }

    private static UnexpectedException unexpectedException(Exception e) {
        return new UnexpectedException("MAGE TAB hack doesn't work", e);
    }

    private static NodeFactory newNodeFactoryHack() throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class<?> clazz = Class.forName("uk.ac.ebi.arrayexpress2.magetab.renderer.adaptor.NodeFactory");
        Constructor<?> constructor = clazz.getDeclaredConstructor();
        constructor.setAccessible(true);
        return (NodeFactory) constructor.newInstance();
    }

}
