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

import com.google.common.base.Predicate;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.configmodel.OntologyTerm;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.OntologyTermGroup;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ExperimentDetailsDto;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.DialogCallback;

import javax.annotation.Nullable;
import java.util.*;

import static uk.ac.ebi.fg.annotare2.web.gwt.editor.client.EditorUtils.dateTimeFormat;
import static uk.ac.ebi.fg.annotare2.web.gwt.editor.client.EditorUtils.dateTimeFormatPlaceholder;

/**
 * @author Olga Melnichuk
 */
public class ExpDetailsViewImpl extends Composite implements ExpDetailsView {

    interface Binder extends UiBinder<Widget, ExpDetailsViewImpl> {
        Binder BINDER = GWT.create(Binder.class);
    }

    @UiField
    TextArea title;

    @UiField
    TextArea description;

    @UiField
    DateBox dateOfExperiment;

    @UiField
    DateBox dateOfPublicRelease;

    @UiField(provided = true)
    ListBox experimentalDesignList;

    @UiField
    Button addExpDesignsButton;

    @UiField
    Button removeExpDesignsButton;

    private Presenter presenter;

    private Map<String, OntologyTerm> experimentalDesigns;

    @Inject
    public ExpDetailsViewImpl() {
        experimentalDesigns = new LinkedHashMap<String, OntologyTerm>();

        experimentalDesignList = new ListBox(true);
        initWidget(Binder.BINDER.createAndBindUi(this));

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
    public void setDetails(ExperimentDetailsDto details) {
        title.setText(details.getTitle());
        description.setText(details.getDescription());
        dateOfExperiment.setValue(details.getExperimentDate());
        dateOfPublicRelease.setValue(details.getPublicReleaseDate());

        experimentalDesigns = new LinkedHashMap<String, OntologyTerm>();
        // todo
        addExperimentalDesigns(Collections.<OntologyTerm>emptyList());
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public ExperimentDetailsDto getDetails() {
        return getResult();
    }

    @UiHandler("title")
    void titleChanged(ChangeEvent event) {
        save();
    }

    @UiHandler("description")
    void descriptionChanged(ChangeEvent event) {
        save();
    }

    @UiHandler("dateOfExperiment")
    void dateOfExperimentChanged(ValueChangeEvent<Date> event) {
        save();
    }

    @UiHandler("dateOfPublicRelease")
    void dateOfPublicReleaseChanged(ValueChangeEvent<Date> event) {
        save();
    }

    @UiHandler("addExpDesignsButton")
    void addExperimentalDesignsClicked(ClickEvent event) {
        if (presenter == null) {
            return;
        }
        presenter.getExperimentalDesigns(new AsyncCallback<List<OntologyTermGroup>>() {
            @Override
            public void onFailure(Throwable caught) {
                //?
            }

            @Override
            public void onSuccess(List<OntologyTermGroup> result) {
                List<OntologyTermGroup> filteredResult = filterExperimentalDesigns(result);
                if (filteredResult.isEmpty()) {
                    // nothing to add
                    return;
                }
                (new ExperimentalDesignsDialog(filteredResult, new DialogCallback<List<OntologyTerm>>() {
                    @Override
                    public void onOkay(List<OntologyTerm> ontologyTerms) {
                        addExperimentalDesigns(ontologyTerms);
                    }
                })).show();
            }
        });
    }

    @UiHandler("removeExpDesignsButton")
    void removeExperimentalDesignsClicked(ClickEvent event) {
        for (int i = 0; i < experimentalDesignList.getItemCount(); i++) {
            if (experimentalDesignList.isItemSelected(i)) {
                experimentalDesigns.remove(experimentalDesignList.getValue(i));
            }
        }
        renderExperimentalDesigns();
    }

    private void addExperimentalDesigns(Collection<OntologyTerm> termsToAdd) {
        for (OntologyTerm term : termsToAdd) {
            experimentalDesigns.put(term.getAccession(), term);
        }
        renderExperimentalDesigns();
    }

    private void renderExperimentalDesigns() {
        experimentalDesignList.clear();
        for (OntologyTerm term : experimentalDesigns.values()) {
            experimentalDesignList.addItem(term.getLabel(), term.getAccession());
        }
    }

    private List<OntologyTermGroup> filterExperimentalDesigns(List<OntologyTermGroup> designGroups) {
        List<OntologyTermGroup> filtered = new ArrayList<OntologyTermGroup>();
        for(OntologyTermGroup group : designGroups) {
            OntologyTermGroup newGroup = group.filter(new Predicate<OntologyTerm>() {
                @Override
                public boolean apply(@Nullable OntologyTerm input) {
                    return input != null && !experimentalDesigns.containsKey(input.getAccession());
                }
            });
            if (!newGroup.isEmpty()) {
                filtered.add(newGroup);
            }
        }
        return filtered;
    }

    private ExperimentDetailsDto getResult() {
        return new ExperimentDetailsDto(
                title.getValue(),
                description.getValue(),
                dateOfExperiment.getValue(),
                dateOfPublicRelease.getValue());
    }

    private void save() {
        presenter.saveDetails(getResult());
    }
}
