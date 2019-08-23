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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.info;

import com.google.common.base.Predicate;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ShowRangeEvent;
import com.google.gwt.event.logical.shared.ShowRangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.submission.model.OntologyTerm;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.ReportingAsyncCallback;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.ReportingAsyncCallback.FailureMessage;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.view.DialogCallback;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.view.NotificationPopupPanel;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.OntologyTermGroup;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ExperimentDetailsDto;

import javax.annotation.Nullable;
import javax.validation.constraints.Size;
import java.util.*;

import static uk.ac.ebi.fg.annotare2.web.gwt.common.client.utils.DatesTimes.dateTimeFormat;
import static uk.ac.ebi.fg.annotare2.web.gwt.common.client.utils.DatesTimes.dateTimeFormatPlaceholder;

/**
 * @author Olga Melnichuk
 */
public class ExperimentDetailsViewImpl extends Composite implements ExperimentDetailsView {

    interface Binder extends UiBinder<Widget, ExperimentDetailsViewImpl> {
        Binder BINDER = GWT.create(Binder.class);
    }

    @UiField
    TextArea title;

    @UiField
    TextArea description;

    @UiField
    @Size(max = 255)

    TextBox relatedAccessionNumber;

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

    @UiField
    ListBox aeExperimentType;

    @UiField
    CheckBox anonymousReview;

    private Presenter presenter;

    private Map<String, OntologyTerm> experimentalDesigns;

    @Inject
    public ExperimentDetailsViewImpl() {
        experimentalDesigns = new LinkedHashMap<>();

        experimentalDesignList = new ListBox(true);
        initWidget(Binder.BINDER.createAndBindUi(this));

        DateBox.DefaultFormat format = new DateBox.DefaultFormat(dateTimeFormat());
        dateOfExperiment.setFormat(format);
        dateOfExperiment.getElement().setPropertyString("placeholder", dateTimeFormatPlaceholder());

        dateOfPublicRelease.setFormat(format);
        dateOfPublicRelease.getElement().setPropertyString("placeholder", dateTimeFormatPlaceholder());
        dateOfPublicRelease.getDatePicker().addShowRangeHandler(new ShowRangeHandler<Date>()
        {
            @Override
            public void onShowRange(final ShowRangeEvent<Date> event)
            {
                final Date today = today();
                Date d = zeroTime(event.getStart());
                final long endTime = event.getEnd().getTime();
                while (d.before(today) && d.getTime() <= endTime)
                {
                    dateOfPublicRelease.getDatePicker().setTransientEnabledOnDates(false, d);
                    d = nextDay(d);
                }
            }
        });
    }

    @Override
    public void setTitle(String title) {
        this.title.setText(title);
    }

    @Override
    public void setDetails(ExperimentDetailsDto details, Collection<String> aeExperimentTypes) {
        setAeExperimentTypeOptions(aeExperimentTypes);

        title.setText(details.getTitle());
        description.setText(details.getDescription());
        dateOfExperiment.setValue(details.getExperimentDate());
        dateOfPublicRelease.setValue(details.getPublicReleaseDate());
        setAeExperimentType(details.getAeExperimentType());

        experimentalDesigns = new LinkedHashMap<>();
        addExperimentalDesigns(details.getExperimentalDesigns());

        anonymousReview.setValue(details.isAnonymousReviewEnabled());
        relatedAccessionNumber.setText(details.getRelatedAccessionNumber());
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
        ExperimentDetailsDto detailsDto = getDetails();
        save(detailsDto);
        title.setValue(detailsDto.getTitle());
    }

    @UiHandler("description")
    void descriptionChanged(ChangeEvent event) {

        ExperimentDetailsDto detailsDto = getDetails();
        save(detailsDto);
        description.setValue(detailsDto.getDescription());
    }

    @UiHandler("relatedAccessionNumber")
    void relatedAccessionNumberChanged(ChangeEvent event) {

        ExperimentDetailsDto detailsDto = getDetails();
        save(detailsDto);
        relatedAccessionNumber.setValue(detailsDto.getRelatedAccessionNumber());
    }



    @UiHandler("dateOfExperiment")
    void dateOfExperimentChanged(ValueChangeEvent<Date> event) {
        save();
    }

    @UiHandler("dateOfPublicRelease")
    void dateOfPublicReleaseChanged(ValueChangeEvent<Date> event) {
        final Date today = today();
        if (event.getValue().before(today))
        {
            dateOfPublicRelease.setValue(today, false);
        }
        if (0 == today.compareTo(dateOfPublicRelease.getValue())) {
            NotificationPopupPanel.warning("The submission will be immediately released to public once loaded to ArrayExpress. Please select a different date if you wish to keep the experiment private.", true, false, 10000);
        }
        save();
    }

    @UiHandler("aeExperimentType")
    void aeExperimentTypeChanged(ChangeEvent event) {
        save();
    }

    @UiHandler("addExpDesignsButton")
    void addExperimentalDesignsClicked(ClickEvent event) {
        if (presenter == null) {
            return;
        }
        presenter.getExperimentalDesigns(
                new ReportingAsyncCallback<List<OntologyTermGroup>>(FailureMessage.UNABLE_TO_LOAD_EXPERIMENTAL_DESIGNS) {
                    @Override
                    public void onSuccess(List<OntologyTermGroup> result) {
                        List<OntologyTermGroup> filteredResult = filterExperimentalDesigns(result);
                        if (filteredResult.isEmpty()) {
                            // nothing to add
                            return;
                        }
                        (new ExperimentalDesignsDialog(filteredResult, new DialogCallback<List<OntologyTerm>>() {
                            @Override
                            public boolean onOk(List<OntologyTerm> ontologyTerms) {
                                addExperimentalDesigns(ontologyTerms);
                                save();
                                return true;
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
        for (OntologyTermGroup group : designGroups) {
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

    private String getAeExperimentType() {
        int index = aeExperimentType.getSelectedIndex();
        //dummy
        if (index == -1) {
            return null;
        }
        return aeExperimentType.getValue(index);
    }

    private void setAeExperimentType(String type) {
        for (int i = 0; i < aeExperimentType.getItemCount(); i++) {
            String value = aeExperimentType.getValue(i);
            if (value.equals(type)) {
                aeExperimentType.setSelectedIndex(i);
                return;
            }
        }
        aeExperimentType.addItem(type);
        aeExperimentType.setSelectedIndex(aeExperimentType.getItemCount()-1);
    }

    private void setAeExperimentTypeOptions(Collection<String> options) {
        aeExperimentType.clear();
        for (String option : options) {
            aeExperimentType.addItem(option);
        }
    }

    private ExperimentDetailsDto getResult() {
        return new ExperimentDetailsDto(
                RegExp.compile("[\\x00-\\x1F]+","g").replace(title.getValue().replaceAll("\\r\\n|[\\r\\n]", " "),"").trim(),
                RegExp.compile("[\\x00-\\x08]*[\\x0B-\\x0C]*[\\x0E-\\x1F]*","g").replace(description.getValue(),"").trim(),
                dateOfExperiment.getValue(),
                dateOfPublicRelease.getValue(),
                getAeExperimentType(),
                experimentalDesigns.values(),
                anonymousReview.getValue(),
                relatedAccessionNumber.getValue());
    }

    private static Date today()
    {
        return zeroTime(new Date());
    }

    private static Date zeroTime(final Date date)
    {
        return new Date(date.getYear(),date.getMonth(),date.getDate());
    }

    private static Date nextDay(final Date date)
    {
        CalendarUtil.addDaysToDate(date, 1);
        return date;
    }

    private void save() {
        presenter.saveDetails(getResult());
    }

    private void save(ExperimentDetailsDto dto) {
        presenter.saveDetails(dto);
    }
}
