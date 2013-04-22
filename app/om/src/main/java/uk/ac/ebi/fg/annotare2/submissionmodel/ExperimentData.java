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

package uk.ac.ebi.fg.annotare2.submissionmodel;

import com.google.common.base.Function;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.transform;
import static com.google.common.collect.Maps.newHashMap;

/**
 * @author Olga Melnichuk
 */
class ExperimentData {

    @JsonProperty("sampleMap")
    private Map<Integer, Sample> sampleMap;

    @JsonProperty("extractMap")
    private Map<Integer, Extract> extractMap;

    @JsonProperty("labeledExtractMap")
    private Map<Integer, LabeledExtract> labeledExtractMap;

    @JsonProperty("assayMap")
    private Map<Integer, Assay> assayMap;

    @JsonProperty("arrayDataFileMap")
    private Map<Integer, ArrayDataFile> arrayDataFileMap;

    @JsonProperty("samples")
    private List<Integer> samples;

    @JsonProperty("extracts")
    private List<Integer> extracts;

    @JsonProperty("labeledExtracts")
    private List<Integer> labeledExtracts;

    @JsonProperty("assays")
    private List<Integer> assays;

    @JsonProperty("arrayDataFiles")
    private List<Integer> arrayDataFiles;

    @JsonProperty("experiment")
    private Experiment experiment;

    @JsonCreator
    ExperimentData(@JsonProperty("experiment") Experiment experiment) {
        this.experiment = experiment;
        saveSamples(experiment);
        saveExtracts(experiment);
        saveLabeledExtracts(experiment);
        saveAssays(experiment);
        saveArrayDataFiles(experiment);
    }

    private void saveExtracts(Experiment experiment) {
        extractMap = newHashMap();
        extracts = newArrayList();
        for (Extract extract : experiment.getExtracts()) {
            extractMap.put(extract.getId(), extract);
            extracts.add(extract.getId());
        }
    }

    private void saveSamples(Experiment experiment) {
        sampleMap = newHashMap();
        samples = newArrayList();
        for (Sample sample : experiment.getSamples()) {
            sampleMap.put(sample.getId(), sample);
            samples.add(sample.getId());
        }
    }

    private void saveLabeledExtracts(Experiment experiment) {
        labeledExtractMap = newHashMap();
        labeledExtracts = newArrayList();
        for (LabeledExtract labeledExtract : experiment.getLabeledExtracts()) {
            labeledExtractMap.put(labeledExtract.getId(), labeledExtract);
            labeledExtracts.add(labeledExtract.getId());
        }
    }

    private void saveAssays(Experiment experiment) {
        assayMap = newHashMap();
        assays = newArrayList();
        for (Assay assay : experiment.getAssays()) {
            assayMap.put(assay.getId(), assay);
            assays.add(assay.getId());
        }
    }

    private void saveArrayDataFiles(Experiment experiment) {
        arrayDataFileMap = newHashMap();
        arrayDataFiles = newArrayList();
        for (ArrayDataFile file : experiment.getArrayDataFiles()) {
            arrayDataFileMap.put(file.getId(), file);
            arrayDataFiles.add(file.getId());
        }
    }

    Experiment fixExperiment() {
        experiment.restoreSamples(transform(samples, new Function<Integer, Sample>() {
            @Nullable
            @Override
            public Sample apply(@Nullable Integer id) {
                return fix(sampleMap.get(id));
            }
        }));
        experiment.restoreExtracts(transform(extracts, new Function<Integer, Extract>() {
            @Nullable
            @Override
            public Extract apply(@Nullable Integer id) {
                return fix(extractMap.get(id));
            }
        }));
        experiment.restoreLabeledExtracts(transform(labeledExtracts, new Function<Integer, LabeledExtract>() {
            @Nullable
            @Override
            public LabeledExtract apply(@Nullable Integer id) {
                return fix(labeledExtractMap.get(id));
            }
        }));
        experiment.restoreAssays(transform(assays, new Function<Integer, Assay>() {
            @Nullable
            @Override
            public Assay apply(@Nullable Integer id) {
                return fix(assayMap.get(id));
            }
        }));
        experiment.restoreArrayDataFiles(transform(arrayDataFiles, new Function<Integer, ArrayDataFile>() {
            @Nullable
            @Override
            public ArrayDataFile apply(@Nullable Integer id) {
                return fix(arrayDataFileMap.get(id));
            }
        }));
        return experiment;
    }

    private Sample fix(Sample sample) {
        sample.setAllExtracts(
                transform(sample.getExtractIds(), new Function<Integer, Extract>() {
                    @Nullable
                    @Override
                    public Extract apply(@Nullable Integer id) {
                        return fix(extractMap.get(id));
                    }
                }));
        return sample;
    }

    private Extract fix(Extract extract) {
        //TODO
        return extract;
    }

    private LabeledExtract fix(LabeledExtract labeledExtract) {
        //TODO
        return labeledExtract;
    }

    private Assay fix(Assay assay) {
        //TODO
        return assay;
    }

    private ArrayDataFile fix(ArrayDataFile file) {
        //TODO
        return file;
    }
}
