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

package uk.ac.ebi.fg.annotare2.web.gwt.user.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.*;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.SubmissionDetails;

import java.util.List;

import static com.google.gwt.i18n.client.DateTimeFormat.getFormat;

/**
 * @author Olga Melnichuk
 */
public class SubmissionListViewImpl extends Composite implements SubmissionListView {

    interface Binder extends UiBinder<Widget, SubmissionListViewImpl> {
    }

    private Presenter presenter;

    private ListDataProvider<SubmissionDetails> dataProvider;

    @UiField(provided = true)
    CellTable<SubmissionDetails> cellTable;

    public SubmissionListViewImpl() {
        cellTable = new CellTable<SubmissionDetails>();
        cellTable.setWidth("100%", true);

        cellTable.addColumn(new TextColumn<SubmissionDetails>() {
            @Override
            public String getValue(SubmissionDetails object) {
                return getFormat(DateTimeFormat.PredefinedFormat.DATE_SHORT).format(object.getCreated());
            }
        }, new TextHeader("Created"));

        cellTable.addColumn(new TextColumn<SubmissionDetails>() {
            @Override
            public String getValue(SubmissionDetails object) {
                return object.getTitle();
            }
        }, new TextHeader("Title"));

        cellTable.addColumn(new TextColumn<SubmissionDetails>() {
            @Override
            public String getValue(SubmissionDetails object) {
                return object.getDescription();
            }
        }, new TextHeader("Description"));

        cellTable.addColumn(new TextColumn<SubmissionDetails>() {
            @Override
            public String getValue(SubmissionDetails object) {
                return "IN PROGRESS"; // TODO
            }
        }, new TextHeader("Status"));

        final SingleSelectionModel<SubmissionDetails> selectionModel = new SingleSelectionModel<SubmissionDetails>(
                new ProvidesKey<SubmissionDetails>() {
                    public Object getKey(SubmissionDetails item) {
                        return item.getId();
                    }
                }
        );

        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            public void onSelectionChange(SelectionChangeEvent event) {
                int id = selectionModel.getSelectedObject().getId();
                GWT.log("onSelectionChange(" + id + ")");
                presenter.onSubmissionSelected(id);
            }
        });

        cellTable.setSelectionModel(selectionModel);

        cellTable.addStyleName("no-cell-borders");

        dataProvider = new ListDataProvider<SubmissionDetails>();
        dataProvider.addDataDisplay(cellTable);

        Binder uiBinder = GWT.create(Binder.class);
        initWidget(uiBinder.createAndBindUi(this));
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    public void setSubmissions(List<SubmissionDetails> submissions) {
        dataProvider.setList(submissions);
    }
}
