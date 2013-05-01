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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.info;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ExperimentDetails;

import java.util.Date;

import static uk.ac.ebi.fg.annotare2.web.gwt.editor.client.EditorUtils.dateTimeFormat;
import static uk.ac.ebi.fg.annotare2.web.gwt.editor.client.EditorUtils.dateTimeFormatPlaceholder;

/**
 * @author Olga Melnichuk
 */
public class ExpDetailsViewImpl extends Composite implements ExpDetailsView {

    interface Binder extends UiBinder<Widget, ExpDetailsViewImpl> {
    }

    @UiField
    TextArea title;

    @UiField
    TextArea description;

    @UiField
    DateBox dateOfExperiment;

    @UiField
    DateBox dateOfPublicRelease;

    private Presenter presenter;

    @Inject
    public ExpDetailsViewImpl() {
        Binder uiBinder = GWT.create(Binder.class);
        initWidget(uiBinder.createAndBindUi(this));

        DateBox.DefaultFormat format = new DateBox.DefaultFormat(dateTimeFormat());
        dateOfExperiment.setFormat(format);
        dateOfExperiment.getElement().setPropertyString("placeholder", dateTimeFormatPlaceholder());

        dateOfPublicRelease.setFormat(format);
        dateOfPublicRelease.getElement().setPropertyString("placeholder", dateTimeFormatPlaceholder());
    }

    @Override
    public void setTitle(String title) {
        this.title.setText(title);
    }

    @Override
    public void setDetails(ExperimentDetails details) {
        title.setText(details.getTitle());
        description.setText(details.getDescription());
        dateOfExperiment.setValue(details.getExperimentDate());
        dateOfPublicRelease.setValue(details.getPublicReleaseDate());
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public ExperimentDetails getDetails() {
        return getResult();
    }

    @UiHandler("title")
    public void titleChanged(ChangeEvent event) {
        save();
    }

    @UiHandler("description")
    public void descriptionChanged(ChangeEvent event) {
        save();
    }

    @UiHandler("dateOfExperiment")
    public void dateOfExperimentChanged(ValueChangeEvent<Date> event) {
        save();
    }

    @UiHandler("dateOfPublicRelease")
    public void dateOfPublicReleaseChanged(ValueChangeEvent<Date> event) {
        save();
    }

    private ExperimentDetails getResult() {
        return new ExperimentDetails(
                title.getValue(),
                description.getValue(),
                dateOfExperiment.getValue(),
                dateOfPublicRelease.getValue());
    }

    private void save() {
        presenter.saveDetails(getResult());
    }
}
