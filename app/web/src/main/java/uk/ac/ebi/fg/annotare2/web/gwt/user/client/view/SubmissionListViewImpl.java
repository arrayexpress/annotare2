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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.*;
import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.UISubmission;
import uk.ac.ebi.fg.annotare2.web.gwt.user.client.resources.ImageResources;
import uk.ac.ebi.fg.annotare2.web.gwt.user.client.view.widget.ClickableImageResourceCell;

import java.util.List;

import static com.google.gwt.i18n.client.DateTimeFormat.getFormat;

/**
 * @author Olga Melnichuk
 */
public class SubmissionListViewImpl extends Composite implements SubmissionListView {

    interface Binder extends UiBinder<Widget, SubmissionListViewImpl> {
    }

    @Inject
    private ImageResources resourceBundle;

    private Presenter presenter;

    private ListDataProvider<UISubmission> dataProvider;

    @UiField(provided = true)
    CellTable<UISubmission> cellTable;

    public SubmissionListViewImpl() {
        cellTable = new CellTable<UISubmission>();
        cellTable.setWidth("100%", true);

        cellTable.addColumn(new TextColumn<UISubmission>() {
            @Override
            public String getValue(UISubmission object) {
                return getFormat(DateTimeFormat.PredefinedFormat.DATE_SHORT).format(object.getCreated());
            }
        }, new TextHeader("Created"));

        cellTable.addColumn(new TextColumn<UISubmission>() {
            @Override
            public String getValue(UISubmission object) {
                //TODO
                String accession = object.getAccession();
                return accession == null ? "UNACCESSIONED" : accession;
            }
        }, new TextHeader("Accession"));

        cellTable.addColumn(new TextColumn<UISubmission>() {
            @Override
            public String getValue(UISubmission object) {
                return object.getTitle();
            }
        }, new TextHeader("Title"));

        cellTable.addColumn(new TextColumn<UISubmission>() {
            @Override
            public String getValue(UISubmission object) {
                return object.getStatus().getTitle();
            }
        }, new TextHeader("Status"));

        Column<UISubmission, ImageResource> editIconColumn =
                new Column<UISubmission, ImageResource>(new ClickableImageResourceCell()){

            @Override
            public ImageResource getValue(UISubmission object) {
                return resourceBundle.editIcon();
            }
        };

        editIconColumn.setFieldUpdater(new FieldUpdater<UISubmission, ImageResource>() {
            public void update(int index, UISubmission object, ImageResource value) {
                Window.alert("To be implemented..");
            }
        });

        cellTable.addColumn(editIconColumn);

        final SingleSelectionModel<UISubmission> selectionModel = new SingleSelectionModel<UISubmission>(
                new ProvidesKey<UISubmission>() {
                    public Object getKey(UISubmission item) {
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
                DefaultSelectionEventManager.<UISubmission>createBlacklistManager(4));

        cellTable.addStyleName("no-cell-borders");

        dataProvider = new ListDataProvider<UISubmission>();
        dataProvider.addDataDisplay(cellTable);

        Binder uiBinder = GWT.create(Binder.class);
        initWidget(uiBinder.createAndBindUi(this));
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    public void setSubmissions(List<UISubmission> submissions) {
        dataProvider.setList(submissions);
    }
}
