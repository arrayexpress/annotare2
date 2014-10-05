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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.design;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.ReportingAsyncCallback;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.ReportingAsyncCallback.FailureMessage;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ProtocolType;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.DialogCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class AddProtocolDialog extends DialogBox {

    interface Binder extends UiBinder<Widget, AddProtocolDialog> {
        Binder BINDER = GWT.create(Binder.class);
    }

    @UiField
    Button cancelButton;

    @UiField
    Button okButton;

    @UiField
    ListBox protocolTypeList;

    @UiField
    Label protocolTypeDefinition;

    private final Presenter presenter;
    private List<ProtocolType> protocolTypes;
    private DialogCallback<ProtocolType> callback;

    public AddProtocolDialog(Presenter presenter, DialogCallback<ProtocolType> callback) {
        this.presenter = presenter;
        this.callback = callback;

        setModal(true);
        setGlassEnabled(true);
        setText("New Protocol");

        setWidget(Binder.BINDER.createAndBindUi(this));

        protocolTypeList.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                setTypeSelection(getSelectedType());
            }
        });

        center();
        loadProtocolTypes();
        setTypeSelection(null);
    }

    @UiHandler("cancelButton")
    void cancelClicked(ClickEvent event) {
        hide();
        if (null != callback) {
            callback.onCancel();
        }
    }

    @UiHandler("okButton")
    void okClicked(ClickEvent event) {
        ProtocolType selected = getSelectedType();
        if (null == selected) {
            return;
        }
        hide();
        if (null != callback) {
            callback.onOkay(selected);
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
    private void loadProtocolTypes() {
        if (presenter == null) {
            return;
        }
        presenter.getProtocolTypes(
                new ReportingAsyncCallback<List<ProtocolType>>(FailureMessage.UNABLE_TO_LOAD_PROTOCOL_TYPES) {
                    @Override
                    public void onSuccess(List<ProtocolType> types) {
                        showProtocolTypes(types);
                    }
        });
    }

    private void showProtocolTypes(List<ProtocolType> types) {
        protocolTypes = new ArrayList<ProtocolType>(types);
        protocolTypeList.clear();
        for (ProtocolType type : types) {
            protocolTypeList.addItem(type.getTerm().getLabel());
        }
        if (!protocolTypes.isEmpty()) {
            protocolTypeList.setItemSelected(0, true);
            DomEvent.fireNativeEvent(Document.get().createChangeEvent(), protocolTypeList);
        }
    }

    private ProtocolType getSelectedType() {
        int index = protocolTypeList.getSelectedIndex();
        return index >= 0 ? protocolTypes.get(index) : null;
    }

    private void setTypeSelection(ProtocolType type) {
        protocolTypeDefinition.setText(type == null ? "" : type.getDefinition());
    }

    public static interface Presenter {
        void getProtocolTypes(AsyncCallback<List<ProtocolType>> callback);
    }
}
