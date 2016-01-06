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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or impl
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.design;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.ReportingAsyncCallback;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.view.DialogCallback;

import static uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.utils.ValidationUtils.integerValuesOnly;

public class AddSamplesDialog extends DialogBox {

    interface Binder extends UiBinder<Widget, AddSamplesDialog> {
        Binder BINDER = GWT.create(Binder.class);
    }

    public interface Presenter {
        void getGeneratedSampleNamesAsync(int numOfSamples, String namingPattern, int startingNumber, AsyncCallback<String> callback);
    }

    class Result {

        public Result(int numOfSamples, String namingPattern, int startingNumber) {
            this.numOfSamples = numOfSamples;
            this.namingPattern = namingPattern;
            this.startingNumber = startingNumber;
        }

        int numOfSamples;
        String namingPattern;
        int startingNumber;
    }

    private DialogCallback<Result> callback;

    private Presenter presenter;

    @UiField
    TextBox numOfSamples;

    @UiField
    TextBox namingPattern;

    @UiField
    TextBox startingNumber;

    @UiField
    InlineLabel preview;

    @UiField
    Button okButton;

    @UiField
    Button cancelButton;

    public AddSamplesDialog(DialogCallback<Result> callback, Presenter presenter) {
        this.callback = callback;
        this.presenter = presenter;

        setModal(true);
        setGlassEnabled(true);
        setText("Add Samples");
        setWidget(Binder.BINDER.createAndBindUi(this));

        integerValuesOnly(numOfSamples);
        integerValuesOnly(startingNumber);

        setDefaultValues();
        updatePreview();
        addHandlers();

        center();
        Scheduler.get().scheduleDeferred(new Command() {
            public void execute() {
                numOfSamples.setFocus(true);
            }
        });
    }

    @UiHandler("okButton")
    void okButtonClicked(ClickEvent event) {
        hide();

        Integer numOfSamples = intValue(this.numOfSamples.getValue());
        String namingPattern = this.namingPattern.getValue();
        Integer startingNumber = intValue(this.startingNumber.getValue());

        if (null != callback && null != numOfSamples && null != namingPattern && null != startingNumber) {
            callback.onOkay(new Result(numOfSamples, namingPattern, startingNumber));
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

    private void setDefaultValues() {
        numOfSamples.setValue("1");
        namingPattern.setValue("Sample #");
        startingNumber.setValue("1");
    }

    private void addHandlers() {
        final KeyUpHandler handler = new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                updatePreview();
            }
        };

        numOfSamples.addKeyUpHandler(handler);
        namingPattern.addKeyUpHandler(handler);
        startingNumber.addKeyUpHandler(handler);
    }

    private void updatePreview() {
        Integer numOfSamples = intValue(this.numOfSamples.getValue());
        String namingPattern = this.namingPattern.getValue();
        Integer startingNumber = intValue(this.startingNumber.getValue());
        if (null != numOfSamples && null != namingPattern && null != startingNumber) {
            presenter.getGeneratedSampleNamesAsync(numOfSamples, namingPattern, startingNumber, new ReportingAsyncCallback<String>() {
                @Override
                public void onFailure(Throwable caught) {
                    super.onFailure(caught);
                    preview.setText("");
                }

                @Override
                public void onSuccess(String result) {
                    if (null != result) {
                        preview.setText(result);
                    }
                }
            });
        }
        preview.setText("");
    }

    private Integer intValue(String value) {
        if (null == value) {
            return null;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
