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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.design;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.*;
import uk.ac.ebi.fg.annotare2.submission.model.EnumWithHelpText;
import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfileType;
import uk.ac.ebi.fg.annotare2.submission.model.FileType;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.view.DialogCallback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssignFilesDialog<T extends EnumWithHelpText> extends DialogBox {

    interface Binder extends UiBinder<Widget, AssignFilesDialog> {
        Binder BINDER = GWT.create(Binder.class);
    }

    private DialogCallback<Map.Entry<FileType, Integer>> callback;

    @UiField
    Button okButton;

    @UiField
    Button cancelButton;

    @UiField
    ListBox columnListBox;

    @UiField
    TextBox noOfColumns;

    @UiField
    Label noOfColumnsLabel;

    @UiField
    HTML columnListHelp;

    private Map<String, FileType> map = new HashMap<>();

    public AssignFilesDialog(DialogCallback<Map.Entry<FileType, Integer>> callback, final List<FileType> values, final ExperimentProfileType experimentProfileType) {
        this.callback = callback;

        setModal(true);
        setGlassEnabled(true);
        setText("Assign Files");

        setWidget(Binder.BINDER.createAndBindUi(this));

        for (FileType t : values) {
            if (t.getClass().isEnum()) {
                columnListBox.addItem(t.getTitle());
                map.put(t.getTitle(), t);
            }
        }

        if(columnListBox.getItemCount() != 0) {
            columnListBox.setSelectedIndex(0);
            columnListHelp.setHTML(values.get(0).getHelpText());
        }
        columnListBox.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                columnListHelp.setHTML(values.get(columnListBox.getSelectedIndex()).getHelpText());
                if(experimentProfileType.isMicroarray() && values.get(columnListBox.getSelectedIndex()).isProcessed()){
                    noOfColumnsLabel.setVisible(true);
                    noOfColumns.setVisible(true);
                }
            }
        });
        if(experimentProfileType.isSequencing() || values.get(columnListBox.getSelectedIndex()).isProcessed()) {
            noOfColumnsLabel.setVisible(true);
            noOfColumns.setVisible(true);
        }
        center();
    }

    @UiHandler("okButton")
    void okButtonClicked(ClickEvent event) {
        Map.Entry<FileType, Integer> selection = new Map.Entry<FileType, Integer>() {
            @Override
            public FileType getKey() {
                return getSelection();
            }

            @Override
            public Integer getValue() {
                if (!noOfColumns.getText().equalsIgnoreCase("")) {
                    return Integer.parseInt(noOfColumns.getText());
                } else if(columnListBox.getItemCount() != 0){
                    return 1;
                } else {
                    return 0;
                }
            }

            @Override
            public Integer setValue(Integer value) {
                return null;
            }
        };

        if (null == selection || selection.getValue() == 0) {
            return;
        }

        hide();
        if (null != callback) {
            callback.onOk(selection);
        }
    }

    @UiHandler("cancelButton")
    void cancelButtonClicked(ClickEvent event) {
        hide();
        if (null != callback) {
            callback.onCancel();
        }
    }

    @Override
    protected void onPreviewNativeEvent(Event.NativePreviewEvent event) {
        super.onPreviewNativeEvent(event);
        if (Event.ONKEYDOWN == event.getTypeInt()) {
            if (KeyCodes.KEY_ESCAPE == event.getNativeEvent().getKeyCode()) {
                hide();
                if (null != callback) {
                    callback.onCancel();
                }
            }
        }
    }

    private FileType getSelection() {
        int index = columnListBox.getSelectedIndex();
        return index >= 0 ? map.get(columnListBox.getValue(index)) : null;
    }
}