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
import uk.ac.ebi.fg.annotare2.submission.model.Protocol;
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
import static java.util.Arrays.asList;

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
    private List<Protocol> selectedProtocolTypes;
    private DialogCallback<List<Protocol>> callback;
    private HashMap<String,TextArea> protocolDescriptions;
    private String sequencingHardware = "454 GS, 454 GS 20, 454 GS FLX, 454 GS FLX+, 454 GS FLX Titanium, 454 GS Junior, Illumina Genome Analyzer, Illumina Genome Analyzer II, Illumina Genome Analyzer IIx, Illumina HiSeq 1000, Illumina HiSeq 1500, Illumina HiSeq 2000, Illumina HiSeq 2500, Illumina HiSeq 3000, Illumina HiSeq 4000, Illumina MiSeq, Illumina HiScanSQ, HiSeq X Five, HiSeq X Ten, NextSeq 500, NextSeq 550, Helicos HeliScope, AB SOLiD System, AB SOLiD System 2.0, AB SOLiD System 3.0, AB SOLiD 3 Plus System, AB SOLiD 4 System, AB SOLiD 4hq System, AB SOLiD PI System, AB 5500 Genetic Analyzer, AB 5500xl Genetic Analyzer, AB 5500xl-W Genetic Analysis System, Complete Genomics, BGISEQ-500, PacBio RS, PacBio RS II, Sequel, Ion Torrent PGM, Ion Torrent Proton, MinION, GridION, AB 3730xL Genetic Analyzer, AB 3730 Genetic Analyzer, AB 3500xL Genetic Analyzer, AB 3500 Genetic Analyzer, AB 3130xL Genetic Analyzer, AB 3130 Genetic Analyzer, AB 310 Genetic Analyzer, unspecified";
    private List<String> sequencingHardwareList;

    public AddProtocolDialog(Presenter presenter, DialogCallback<List<Protocol>> callback) {
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

        sequencingHardwareList = new ArrayList<>();
        sequencingHardwareList = asList(sequencingHardware.split("\\s*,\\s*"));

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

        List<Protocol> selectedProtocols = selectedProtocolTypes;

        boolean descriptionIsNull = false;

        for (Protocol protocol:
                selectedProtocolTypes) {
            protocol.setDescription(protocolDescriptions.get(protocol.getType().getLabel()).getValue());
        }

        for (Protocol protocol:
                selectedProtocolTypes) {
            if(isNullOrEmpty(protocol.getDescription()))
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
                Protocol protocol = new Protocol(1);
                if(optionalProtocols.contains(entry.getKey().toString()))
                {
                    for (ProtocolType type:
                         protocolTypes) {
                        if(entry.getKey().toString().equalsIgnoreCase(type.getTerm().getLabel()))
                        {
                            protocol.setType(type.getTerm());
                            protocol.setDescription(entry.getValue().getValue());
                            selectedProtocols.add(protocol);
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

        addProtocolsToPanel(mandatoryProtocols, protocolsPanel, protocolDescriptions);

        Label optionalProtocolsLabel = new Label("Optional Protocols");
        optionalProtocolsLabel.addStyleName("optionalProtocolHeaderLabel");
        optionalProtocolsLabel.addStyleName("optionalProtocolHeader");

        protocolsPanel.add(optionalProtocolsLabel);

        addProtocolsToPanel(optionalProtocols, protocolsPanel, protocolDescriptions);

        selectedProtocolTypes.clear();
        for (ProtocolType type : types) {
            Protocol protocol = new Protocol(1);
            if (mandatoryProtocols.contains(type.getTerm().getLabel())) {
                protocol.setType(type.getTerm());

                selectedProtocolTypes.add(protocol);
            }
        }
    }

    protected static void addProtocolsToPanel(List<String> protocols, VerticalPanel protocolsPanel,
                                              HashMap<String,TextArea> protocolDescriptions) {
        for (String protocol: protocols) {
            PlaceholderTextArea textBox = new PlaceholderTextArea();

            textBox.setWidth("95%");
            textBox.setStyleName("protocol_description_textArea_resize");
            textBox.setName(protocol+"description");
            textBox.setPlaceholder("Please enter the description for the "+ protocol);

            protocolDescriptions.put(protocol,textBox);

            Label protocolLabel = new Label(protocol);
            protocolLabel.addStyleName("protocolLabel");

            protocolsPanel.add(protocolLabel);
            protocolsPanel.add(textBox);
        }
    }

    public interface Presenter {
        void getProtocolTypes(AsyncCallback<ArrayList<ProtocolType>> callback);
    }
}
