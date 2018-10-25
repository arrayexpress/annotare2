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
    SINGLE_CELL_SEQUENCING("Single-cell sequencing"),
    PLANT_ONE_COLOR_MICROARRAY("Plant - One-color microarray"),
    PLANT_TWO_COLOR_MICROARRAY("Plant - Two-color microarray"),
    PLANT_SEQUENCING("Plant - High-throughput sequencing"),
    SINGLE_CELL_PLANT_SEQUENCING("Plant - Single-cell sequencing"),
    HUMAN_ONE_COLOR_MICROARRAY("Human - One-color microarray"),
    HUMAN_TWO_COLOR_MICROARRAY("Human - Two-color microarray"),
    HUMAN_SEQUENCING("Human - High-throughput sequencing"),
    SINGLE_CELL_HUMAN_SEQUENCING("Human - Single-cell sequencing"),
    ANIMAL_ONE_COLOR_MICROARRAY("Animal - One-color microarray"),
    ANIMAL_TWO_COLOR_MICROARRAY("Animal - Two-color microarray"),
    ANIMAL_SEQUENCING("Animal - High-throughput sequencing"),
    SINGLE_CELL_ANIMAL_SEQUENCING("Animal - Single-cell sequencing"),
    CELL_LINE_ONE_COLOR_MICROARRAY("Cell line - One-color microarray"),
    CELL_LINE_TWO_COLOR_MICROARRAY("Cell line - Two-color microarray"),
    CELL_LINE_SEQUENCING("Cell line - High-throughput sequencing"),
    SINGLE_CELL_CELL_LINE_SEQUENCING("Cell line - Single-cell sequencing");

    private final String title;

    ExperimentProfileType(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public boolean isMicroarray() {
        return ONE_COLOR_MICROARRAY == this ||
                TWO_COLOR_MICROARRAY == this ||
                PLANT_ONE_COLOR_MICROARRAY == this ||
                PLANT_TWO_COLOR_MICROARRAY == this ||
                HUMAN_ONE_COLOR_MICROARRAY == this ||
                HUMAN_TWO_COLOR_MICROARRAY == this ||
                ANIMAL_ONE_COLOR_MICROARRAY == this ||
                ANIMAL_TWO_COLOR_MICROARRAY == this ||
                CELL_LINE_ONE_COLOR_MICROARRAY == this ||
                CELL_LINE_TWO_COLOR_MICROARRAY == this;
    }

    public boolean isTwoColorMicroarray() {
        return TWO_COLOR_MICROARRAY == this ||
                PLANT_TWO_COLOR_MICROARRAY == this ||
                HUMAN_TWO_COLOR_MICROARRAY == this ||
                ANIMAL_TWO_COLOR_MICROARRAY == this ||
                CELL_LINE_TWO_COLOR_MICROARRAY == this;
    }

    public boolean isSequencing() {
        return SEQUENCING == this ||
                SINGLE_CELL_SEQUENCING == this ||
                PLANT_SEQUENCING == this ||
                SINGLE_CELL_PLANT_SEQUENCING == this ||
                HUMAN_SEQUENCING == this ||
                SINGLE_CELL_HUMAN_SEQUENCING == this ||
                ANIMAL_SEQUENCING == this ||
                SINGLE_CELL_ANIMAL_SEQUENCING == this ||
                CELL_LINE_SEQUENCING == this ||
                SINGLE_CELL_CELL_LINE_SEQUENCING == this;
    }

    public boolean isSingleCell() {
        return SINGLE_CELL_SEQUENCING == this ||
                SINGLE_CELL_PLANT_SEQUENCING == this ||
                SINGLE_CELL_HUMAN_SEQUENCING == this ||
                SINGLE_CELL_ANIMAL_SEQUENCING == this ||
                SINGLE_CELL_CELL_LINE_SEQUENCING == this;
    }
}
