/*
 * Copyright 2009-2014 European Molecular Biology Laboratory
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

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.*;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.*;
import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.SubmissionRow;
import uk.ac.ebi.fg.annotare2.web.gwt.user.client.resources.ImageResources;
import uk.ac.ebi.fg.annotare2.web.gwt.user.client.view.widget.ClickableImageResourceCell;

import java.util.List;

import static com.google.gwt.i18n.client.DateTimeFormat.getFormat;
import static uk.ac.ebi.fg.annotare2.web.gwt.user.client.view.Utils.openSubmissionEditor;

/**
 * @author Olga Melnichuk
 */
public class SubmissionListViewImpl extends Composite implements SubmissionListView {

    interface Binder extends UiBinder<Widget, SubmissionListViewImpl> {
    }

    @Inject
    private ImageResources resourceBundle;

    private Presenter presenter;

    private ListDataProvider<SubmissionRow> dataProvider;

    @UiField(provided = true)
    DataGrid<SubmissionRow> dataGrid;

    public SubmissionListViewImpl() {
        dataGrid = new CustomDataGrid<SubmissionRow>(Integer.MAX_VALUE, false);

        dataGrid.addColumn(new TextColumn<SubmissionRow>() {
            @Override
            public String getValue(SubmissionRow object) {
                return getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_SHORT).format(object.getCreated());
            }
        }, new TextHeader("Created on"));

        /* TODO: uncomment when we have array submissions
        dataGrid.addColumn(new TextColumn<SubmissionRow>() {
            @Override
            public String getValue(SubmissionRow object) {
                return object.getType().getTitle();
            }
        }, new TextHeader("Type"));
        */

        dataGrid.addColumn(new TextColumn<SubmissionRow>() {
            @Override
            public String getValue(SubmissionRow object) {
                return object.getAccession();
            }
        }, new TextHeader("Accession"));

        dataGrid.addColumn(new TextColumn<SubmissionRow>() {
            @Override
            public String getValue(SubmissionRow object) {
                return object.getTitle();
            }
        }, new TextHeader("Title"));

        dataGrid.addColumn(new TextColumn<SubmissionRow>() {
            @Override
            public String getValue(SubmissionRow object) {
                return object.getStatus().getTitle();
            }
        }, new TextHeader("Status"));

        Column<SubmissionRow, ImageResource> editIconColumn =
                new Column<SubmissionRow, ImageResource>(new ClickableImageResourceCell()) {

                    @Override
                    public ImageResource getValue(SubmissionRow object) {
                        return resourceBundle.editIcon();
                    }
                };

        editIconColumn.setFieldUpdater(new FieldUpdater<SubmissionRow, ImageResource>() {
            public void update(int index, SubmissionRow row, ImageResource value) {
                openSubmissionEditor(row.getId());
            }
        });

        dataGrid.addColumn(editIconColumn);

        dataGrid.setColumnWidth(0, 11, Style.Unit.EM);
        dataGrid.setColumnWidth(1, 11, Style.Unit.EM);
        dataGrid.setColumnWidth(3, 14, Style.Unit.EM);
        dataGrid.setColumnWidth(4, 5, Style.Unit.EM);

        final SingleSelectionModel<SubmissionRow> selectionModel = new SingleSelectionModel<SubmissionRow>(
                new ProvidesKey<SubmissionRow>() {
                    public Object getKey(SubmissionRow item) {
                        return item.getId();
                    }
                }
        );

        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            public void onSelectionChange(SelectionChangeEvent event) {
                long id = selectionModel.getSelectedObject().getId();
                GWT.log("onSelectionChange(" + id + ")");
                presenter.onSubmissionSelected(id);
            }
        });

        //todo create column black list dynamically
        dataGrid.setSelectionModel(selectionModel,
                DefaultSelectionEventManager.<SubmissionRow>createBlacklistManager(4));

        dataGrid.addStyleName("no-cell-borders");

        dataProvider = new ListDataProvider<SubmissionRow>();
        dataProvider.addDataDisplay(dataGrid);

        Binder uiBinder = GWT.create(Binder.class);
        initWidget(uiBinder.createAndBindUi(this));
    }

    public void setCurator(boolean isCurator) {
        if (isCurator) {
            dataGrid.insertColumn(0, new TextColumn<SubmissionRow>() {
                @Override
                public String getValue(SubmissionRow object) {
                    return object.getUserEmail();
                }
            }, new TextHeader("Created by"));
            dataGrid.setColumnWidth(0, 12, Style.Unit.EM);
            dataGrid.setColumnWidth(1, 11, Style.Unit.EM);
            dataGrid.setColumnWidth(2, 11, Style.Unit.EM);
            dataGrid.clearColumnWidth(3);
            dataGrid.setColumnWidth(4, 14, Style.Unit.EM);
            dataGrid.setColumnWidth(5, 5, Style.Unit.EM);
        }
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    public void setSubmissions(List<SubmissionRow> submissions) {
        dataProvider.setList(submissions);
    }
}
