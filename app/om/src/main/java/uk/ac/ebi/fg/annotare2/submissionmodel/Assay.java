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
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import javax.annotation.Nullable;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.transform;
import static java.util.Collections.unmodifiableList;

/**
 * @author Olga Melnichuk
 */
public class Assay implements GraphNode {

    @JsonProperty("id")
    private int id;

    @JsonProperty("name")
    private String name;

    private List<ArrayDataFile> arrayDataFiles;
    private List<Integer> arrayDataFileIds;

    private List<Scan> scans;
    private List<Integer> scanIds;

    @JsonCreator
    public Assay(@JsonProperty("id") int id) {
        this.id = id;
        arrayDataFiles = newArrayList();
        scans = newArrayList();
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addArrayDataFile(ArrayDataFile dataFile) {
        arrayDataFiles.add(dataFile);
    }

    public void addScan(Scan scan) {
        scans.add(scan);
    }

    @JsonIgnore
    public List<ArrayDataFile> getArrayDataFiles() {
        return unmodifiableList(arrayDataFiles);
    }

    @JsonIgnore
    public List<Scan> getScans() {
        return unmodifiableList(scans);
    }

    @JsonProperty("arrayDataFiles")
    List<Integer> getArrayDataFileIds() {
        return arrayDataFileIds != null ? arrayDataFileIds :
                transform(arrayDataFiles, new Function<ArrayDataFile, Integer>() {
                    @Nullable
                    @Override
                    public Integer apply(@Nullable ArrayDataFile arrayDataFile) {
                        return arrayDataFile.getId();
                    }
                });
    }

    @JsonProperty("scans")
    List<Integer> getScansIds() {
        return scanIds != null ? scanIds :
                transform(scans, new Function<Scan, Integer>() {
                    @Nullable
                    @Override
                    public Integer apply(@Nullable Scan scan) {
                        return scan.getId();
                    }
                });

    }

    @JsonProperty("arrayDataFiles")
    void setArrayDataFileIds(List<Integer> arrayDataFileIds) {
        this.arrayDataFileIds = newArrayList(arrayDataFileIds);
    }

    @JsonProperty("scans")
    void setScanIds(List<Integer> scanIds) {
        this.scanIds = newArrayList(scanIds);
    }

    void setAllArrayDataFiles(List<ArrayDataFile> dataFiles) {
        this.arrayDataFiles = newArrayList(dataFiles);
        this.arrayDataFileIds = null;
    }

    void setAllScans(List<Scan> scans) {
        this.scans = newArrayList(scans);
        this.scanIds = null;
    }
}
