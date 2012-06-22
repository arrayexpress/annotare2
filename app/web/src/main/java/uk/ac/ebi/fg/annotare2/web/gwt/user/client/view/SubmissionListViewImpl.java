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

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.*;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.SubmissionInfo;

import java.util.Collection;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class SubmissionListViewImpl extends Composite implements SubmissionListView {

    interface Binder extends UiBinder<Widget, SubmissionListViewImpl> {
    }

    private Presenter presenter;

    private ListDataProvider<SubmissionInfo> dataProvider;

    @UiField(provided = true)
    CellTable<SubmissionInfo> cellTable;

    public SubmissionListViewImpl() {
        cellTable = new CellTable<SubmissionInfo>();
        cellTable.setWidth("100%", true);
        cellTable.addColumn(new TextColumn<SubmissionInfo>() {
            @Override
            public String getValue(SubmissionInfo object) {
                return object.getTitle();
            }
        }, new TextHeader("Title"));

        cellTable.addColumn(new TextColumn<SubmissionInfo>() {
            @Override
            public String getValue(SubmissionInfo object) {
                return object.getDescription();
            }
        }, new TextHeader("Description"));


        final SingleSelectionModel<SubmissionInfo> selectionModel = new SingleSelectionModel<SubmissionInfo>(
                new ProvidesKey<SubmissionInfo>() {
                    public Object getKey(SubmissionInfo item) {
                        return item.getId();
                    }
                }
        );
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            public void onSelectionChange(SelectionChangeEvent event) {
                GWT.log("onSelectionChange(" + selectionModel.getSelectedObject().getId() + ")");
            }
        });
        cellTable.setSelectionModel(selectionModel);

        cellTable.addStyleName("no-cell-borders");

        dataProvider = new ListDataProvider<SubmissionInfo>();
        dataProvider.addDataDisplay(cellTable);

        Binder uiBinder = GWT.create(Binder.class);
        initWidget(uiBinder.createAndBindUi(this));
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    public void setSubmissions(List<SubmissionInfo> submissions) {
        dataProvider.setList(submissions);
    }
}
