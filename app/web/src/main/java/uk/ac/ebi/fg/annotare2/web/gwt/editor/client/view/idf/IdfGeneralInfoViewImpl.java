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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.idf;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;

import java.util.Date;

/**
 * @author Olga Melnichuk
 */
public class IdfGeneralInfoViewImpl extends Composite implements IdfGeneralInfoView {

    private static DateTimeFormat DATE_FORMAT = DateTimeFormat.getFormat("yyyy-MM-dd");

    interface Binder extends UiBinder<HTMLPanel, IdfGeneralInfoViewImpl> {
    }

    @UiField
    TextArea title;

    @UiField
    TextArea description;

    @UiField
    TextBox dateOfExperiment;

    @UiField
    TextBox dateOfPublicRelease;

    public IdfGeneralInfoViewImpl() {
        Binder uiBinder = GWT.create(Binder.class);
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void setTitle(String title) {
        this.title.setText(title);
    }

    @Override
    public void setDescription(String description) {
        this.description.setText(description);
    }

    @Override
    public void setDateOfExperiment(Date date) {
        this.dateOfExperiment.setValue(dateToString(date));
    }

    @Override
    public void setDateOfPublicRelease(Date date) {
        this.dateOfPublicRelease.setValue(dateToString(date));
    }

    private static String dateToString(Date date) {
        return date == null ? "" : DATE_FORMAT.format(date);
    }
}
