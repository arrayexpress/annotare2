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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.arraydesign.header;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.datepicker.client.DateBox;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.ComboBox;

import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class AdfGeneralInfoViewImpl extends Composite implements AdfGeneralInfoView {

    @UiField
    TextBox adName;

    @UiField
    TextBox adVersion;

    @UiField
    ComboBox adTechnologyType;

    @UiField
    ComboBox adSubstrateType;

    @UiField
    ComboBox adSurfaceType;

    @UiField
    ComboBox adSpecies;

    @UiField
    TextArea adDescription;

    @UiField
    DateBox adPublicReleaseDate;

    interface Binder extends UiBinder<HTMLPanel, AdfGeneralInfoViewImpl> {
        Binder BINDER = GWT.create(Binder.class);
    }

    public AdfGeneralInfoViewImpl() {
        initWidget(Binder.BINDER.createAndBindUi(this));
    }

    @Override
    public void setTechnologyTypes(List<String> types) {
        adTechnologyType.setOptions(types);
    }

    @Override
    public void setSubstrateTypes(List<String> types) {
        adSubstrateType.setOptions(types);
    }

    @Override
    public void setSurfaceType(List<String> types) {
        adSurfaceType.setOptions(types);
    }

    @Override
    public void setSpecies(List<String> species) {
        adSpecies.setOptions(species);
    }

}
