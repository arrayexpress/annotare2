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
import com.google.gwt.event.dom.client.*;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.ui.Label;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.ReportingAsyncCallback;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.ReportingAsyncCallback.FailureMessage;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.view.DialogCallback;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ProtocolDetail;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ProtocolType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * @author Olga Melnichuk
 */
public class AddProtocolDialog extends DialogBox {

    interface Binder extends UiBinder<Widget, AddProtocolDialog> {
        Binder BINDER = GWT.create(Binder.class);
    }

    @UiField
    VerticalPanel protocolsPanel;

    @UiField
    Label descriptionNotNullLabel;

    private final Presenter presenter;
    private List<ProtocolType> protocolTypes;
    private List<String> mandatoryProtocols;
    private List<String> optionalProtocols;
    private List<ProtocolDetail> selectedProtocolTypes;
    private DialogCallback<List<ProtocolDetail>> callback;
    private HashMap<String,TextArea> protocolDescriptions;

    public AddProtocolDialog(Presenter presenter, DialogCallback<List<ProtocolDetail>> callback) {
        this.presenter = presenter;
        this.callback = callback;

        setModal(true);
        setGlassEnabled(true);
        setText("New Protocol");

        setWidget(Binder.BINDER.createAndBindUi(this));

        loadProtocolTypes();

        mandatoryProtocols = new ArrayList<>();
        selectedProtocolTypes = new ArrayList<>();
        optionalProtocols = new ArrayList<>();
        protocolDescriptions = new HashMap<>();

        center();
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

        List<ProtocolDetail> selectedProtocols = selectedProtocolTypes;

        boolean descriptionIsNull = false;

        for (ProtocolDetail detail:
                selectedProtocolTypes) {
            detail.setProtocolDescription(protocolDescriptions.get(detail.getProtocolType().getTerm().getLabel()).getValue());
        }

        for (ProtocolDetail detail:
                selectedProtocolTypes) {
            if(isNullOrEmpty(detail.getProtocolDescription()))
            {
                descriptionIsNull = true;
                break;
            }
        }

        if(descriptionIsNull)
        {
            descriptionNotNullLabel.setVisible(true);
            return;
        }


        for(Map.Entry<String,TextArea> entry: protocolDescriptions.entrySet() )
        {
            if(!isNullOrEmpty(entry.getValue().getValue()))
            {
                ProtocolDetail detail = new ProtocolDetail();
                if(optionalProtocols.contains(entry.getKey().toString()))
                {
                    for (ProtocolType type:
                            protocolTypes) {
                        if(entry.getKey().toString().equalsIgnoreCase(type.getTerm().getLabel()))
                        {
                            detail.setProtocolType(type);
                            detail.setProtocolDescription(entry.getValue().getValue());
                            selectedProtocols.add(detail);
                        }
                    }
                }
            }
        }

        hide();

        if (null != callback) {
            callback.onOk(selectedProtocols);
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
                new ReportingAsyncCallback<ArrayList<ProtocolType>>(FailureMessage.UNABLE_TO_LOAD_PROTOCOL_TYPES) {
                    @Override
                    public void onSuccess(ArrayList<ProtocolType> types) {
                        showProtocolTypes(types);
                    }
                });
    }

    private void showProtocolTypes(List<ProtocolType> types) {
        protocolTypes = new ArrayList<>(types);

        List<String> protocolTypesName = new ArrayList<>();

        for (ProtocolType type:
                protocolTypes) {
            protocolTypesName.add(type.getTerm().getLabel());
        }

        for (MandatoryProtocols protocol:
                MandatoryProtocols.values()) {
            if(protocolTypesName.contains(protocol.getName()))
                mandatoryProtocols.add(protocol.getName());
        }

        for (OptionalProtocols protocol:
                OptionalProtocols.values()) {
            if(protocolTypesName.contains(protocol.getName()))
                optionalProtocols.add(protocol.getName());
        }

        for (String protocol:
                mandatoryProtocols) {

            PlaceholderTextArea textBox = new PlaceholderTextArea();

            textBox.setWidth("95%");
            textBox.setName(protocol+"description");
            textBox.setPlaceholder("Please enter the description of "+ protocol);

            protocolDescriptions.put(protocol,textBox);

            Label protocolLabel = new Label(protocol);
            protocolLabel.addStyleName("protocolLabel");

            protocolsPanel.add(protocolLabel);
            protocolsPanel.add(textBox);
        }

        Label optionalProtocolsLabel = new Label("Optional Protocols");
        optionalProtocolsLabel.addStyleName("optionalProtocolHeaderLabel");
        optionalProtocolsLabel.addStyleName("optionalProtocolHeader");

        protocolsPanel.add(optionalProtocolsLabel);

        for (String protocol:
                optionalProtocols) {
            PlaceholderTextArea textBox = new PlaceholderTextArea();

            textBox.setWidth("95%");
            textBox.setName(protocol+"description");
            textBox.setPlaceholder("Please enter the description of "+ protocol);

            protocolDescriptions.put(protocol,textBox);

            Label protocolLabel = new Label(protocol);
            protocolLabel.addStyleName("protocolLabel");

            protocolsPanel.add(protocolLabel);
            protocolsPanel.add(textBox);
        }

        selectedProtocolTypes.clear();
        for (ProtocolType type : types) {
            ProtocolDetail detail = new ProtocolDetail();
            if (mandatoryProtocols.contains(type.getTerm().getLabel())) {
                detail.setProtocolType(type);

                selectedProtocolTypes.add(detail);
            }
        }
    }

    public interface Presenter {
        void getProtocolTypes(AsyncCallback<ArrayList<ProtocolType>> callback);
    }
}