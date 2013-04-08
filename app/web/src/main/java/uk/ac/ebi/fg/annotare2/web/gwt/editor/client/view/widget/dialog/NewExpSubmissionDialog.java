/*
 * Copyright 2009-2013 European Molecular Biology Laboratory
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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.dialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Olga Melnichuk
 */
public class NewExpSubmissionDialog extends DialogBox {

    interface Binder extends UiBinder<Widget, NewExpSubmissionDialog> {
        Binder BINDER = GWT.create(Binder.class);
    }

    private static final String ONE_COLOR = "1-color";
    private static final String TWO_COLOR = "2-color";
    private static final String SEQ = "seq";

    @UiField
    ScrollPanel templateDetails;

    @UiField
    ListBox templateBox;

    @UiField
    Button cancelButton;

    @UiField
    Button okButton;

    private final Map<String, Widget> details = new HashMap<String, Widget>();

    public NewExpSubmissionDialog() {
        setModal(true);
        setGlassEnabled(true);
        setText("New Experiment Submission");

        setWidget(Binder.BINDER.createAndBindUi(this));
        templateBox.addItem("One-color microarray", ONE_COLOR);
        templateBox.addItem("Two-color microarray", TWO_COLOR);
        templateBox.addItem("High-throughput sequencing", SEQ);
        templateBox.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                showDetails(templateBox.getValue(templateBox.getSelectedIndex()));
            }
        });
        center();

        selectFirstTemplate();
    }

    @UiHandler("cancelButton")
    public void onCancelButtonClick(ClickEvent event) {
        hide();
    }

    @UiHandler("okButton")
    public void onOkButtonClick(ClickEvent event) {
        hide();
    }

    private void showDetails(String key) {
        Widget w = details.get(key);
        if (w == null) {
            w = createDetails(key);
            details.put(key, w);
        }
        templateDetails.setWidget(w);
    }

    private Widget createDetails(String key) {
        if (ONE_COLOR.equals(key)) {
            return new OneColorMicroarrayView();
        } else if (TWO_COLOR.equals(key)) {
            return new TwoColorMicroarrayView();
        } else if (SEQ.equals(key)) {
            return new Label("HTS submission is...");
        } else {
            throw new IllegalArgumentException("Unknown key: " + key);
        }
    }

    private void selectFirstTemplate() {
        templateBox.setItemSelected(0, true);
        DomEvent.fireNativeEvent(Document.get().createChangeEvent(), templateBox);
    }

}
