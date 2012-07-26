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

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.*;
import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.UISubmissionRow;
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

    private ListDataProvider<UISubmissionRow> dataProvider;

    @UiField(provided = true)
    CellTable<UISubmissionRow> cellTable;

    public SubmissionListViewImpl() {
        cellTable = new CellTable<UISubmissionRow>();
        cellTable.setWidth("100%", true);

        cellTable.addColumn(new TextColumn<UISubmissionRow>() {
            @Override
            public String getValue(UISubmissionRow object) {
                return getFormat(DateTimeFormat.PredefinedFormat.DATE_SHORT).format(object.getCreated());
            }
        }, new TextHeader("Created"));

        cellTable.addColumn(new TextColumn<UISubmissionRow>() {
            @Override
            public String getValue(UISubmissionRow object) {
                //TODO
                String accession = object.getAccession();
                return accession == null ? "UNACCESSIONED" : accession;
            }
        }, new TextHeader("Accession"));

        cellTable.addColumn(new TextColumn<UISubmissionRow>() {
            @Override
            public String getValue(UISubmissionRow object) {
                return object.getTitle();
            }
        }, new TextHeader("Title"));

        cellTable.addColumn(new TextColumn<UISubmissionRow>() {
            @Override
            public String getValue(UISubmissionRow object) {
                return object.getStatus().getTitle();
            }
        }, new TextHeader("Status"));

        Column<UISubmissionRow, ImageResource> editIconColumn =
                new Column<UISubmissionRow, ImageResource>(new ClickableImageResourceCell()) {

                    @Override
                    public ImageResource getValue(UISubmissionRow object) {
                        return resourceBundle.editIcon();
                    }
                };

        editIconColumn.setFieldUpdater(new FieldUpdater<UISubmissionRow, ImageResource>() {
            public void update(int index, UISubmissionRow object, ImageResource value) {
                openSubmissionEditor(0);
            }
        });

        cellTable.addColumn(editIconColumn);

        final SingleSelectionModel<UISubmissionRow> selectionModel = new SingleSelectionModel<UISubmissionRow>(
                new ProvidesKey<UISubmissionRow>() {
                    public Object getKey(UISubmissionRow item) {
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

        //todo create column black list dynamically
        cellTable.setSelectionModel(selectionModel,
                DefaultSelectionEventManager.<UISubmissionRow>createBlacklistManager(4));

        cellTable.addStyleName("no-cell-borders");

        dataProvider = new ListDataProvider<UISubmissionRow>();
        dataProvider.addDataDisplay(cellTable);

        Binder uiBinder = GWT.create(Binder.class);
        initWidget(uiBinder.createAndBindUi(this));
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    public void setSubmissions(List<UISubmissionRow> submissions) {
        dataProvider.setList(submissions);
    }
}
