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

package uk.ac.ebi.fg.annotare2.web.gwt.common.shared;

import com.google.gwt.user.client.rpc.IsSerializable;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.dto.EfoTermDto;

/**
 * @author Olga Melnichuk
 */
public class SystemEfoTermsDto implements IsSerializable {

    private EfoTermDto organismTerm;
    private EfoTermDto organismPartTerm;
    private EfoTermDto unitTerm;
    private EfoTermDto materialTypeTerm;
    private EfoTermDto studyDesignTerm;

    public EfoTermDto getOrganismTerm() {
        return organismTerm;
    }

    public void setOrganismTerm(EfoTermDto organismTerm) {
        this.organismTerm = organismTerm;
    }

    public EfoTermDto getOrganismPartTerm() {
        return organismPartTerm;
    }

    public void setOrganismPartTerm(EfoTermDto organismPartTerm) {
        this.organismPartTerm = organismPartTerm;
    }

    public EfoTermDto getUnitTerm() {
        return unitTerm;
    }

    public void setUnitTerm(EfoTermDto unitTerm) {
        this.unitTerm = unitTerm;
    }

    public EfoTermDto getMaterialTypeTerm() {
        return materialTypeTerm;
    }

    public void setMaterialTypeTerm(EfoTermDto materialTypeTerm) {
        this.materialTypeTerm = materialTypeTerm;
    }

    public EfoTermDto getStudyDesignTerm() {
        return studyDesignTerm;
    }

    public void setStudyDesignTerm(EfoTermDto studyDesignTerm) {
        this.studyDesignTerm = studyDesignTerm;
    }
}
