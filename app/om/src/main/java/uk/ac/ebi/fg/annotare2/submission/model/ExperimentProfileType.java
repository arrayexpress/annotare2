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

package uk.ac.ebi.fg.annotare2.submission.model;

/**
 * @author Olga Melnichuk
 */
public enum ExperimentProfileType {
    ONE_COLOR_MICROARRAY("One-color microarray"),
    TWO_COLOR_MICROARRAY("Two-color microarray"),
    SEQUENCING("High-throughput sequencing"),
    PLANT_ONE_COLOR_MICROARRAY("Plant - One-color microarray"),
    PLANT_TWO_COLOR_MICROARRAY("Plant - Two-color microarray"),
    PLANT_SEQUENCING("Plant - High-throughput sequencing");

    private final String title;

    private ExperimentProfileType(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public boolean isMicroarray() {
        return this == ONE_COLOR_MICROARRAY || isTwoColorMicroarray();
    }

    public boolean isTwoColorMicroarray() {
        return this == TWO_COLOR_MICROARRAY;
    }

    public boolean isSequencing() {
        return SEQUENCING == this;
    }

    public boolean isPlantSequncing() {return PLANT_SEQUENCING == this;}

    public boolean isPlantMicroarray()  {
        return this == PLANT_ONE_COLOR_MICROARRAY || isPlantTwoColorMicroarray();
    }

    public boolean isPlantTwoColorMicroarray() { return this == PLANT_TWO_COLOR_MICROARRAY; }
}
