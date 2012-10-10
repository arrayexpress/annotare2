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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ToggleButton;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.AsyncEventFinishListener;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.ImportFileDialog;

/**
 * @author Olga Melnichuk
 */
public class IdfTabToolBarViewImpl extends Composite implements IdfTabToolBarView {

    interface Binder extends UiBinder<HTMLPanel, IdfTabToolBarViewImpl> {
    }

    @UiField
    Button importButton;

    @UiField(provided = true)
    ToggleButton viewModeButton = new ToggleButton("Switch to table", "Switch to structure");

    private Presenter presenter;

    private ImportFileDialog importFileDialog;

    public IdfTabToolBarViewImpl() {
        Binder uiBinder = GWT.create(Binder.class);
        initWidget(uiBinder.createAndBindUi(this));

        viewModeButton.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                presenter.switchToSheetMode(event.getValue());
            }
        });

        importButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                importFileDialog = new ImportFileDialog("Import Investigation Design...");
                importFileDialog.addImportFileDialogHandler(new ImportFileDialog.Handler() {
                    public void onImport(String fileName, AsyncEventFinishListener listener) {
                        presenter.importFile(listener);
                    }
                });
                importFileDialog.show();
            }
        });
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setSheetModeOn(boolean yesNo) {
        this.viewModeButton.setValue(yesNo, false);
    }
}
