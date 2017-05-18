package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.design;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.ReportingAsyncCallback;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.view.DialogCallback;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ProtocolDetail;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ProtocolType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * Created by haideri on 17/05/2017.
 */
public class AddOptionalProtocolDialog extends DialogBox {

    interface Binder extends UiBinder<Widget, AddOptionalProtocolDialog> {
        AddOptionalProtocolDialog.Binder BINDER = GWT.create(AddOptionalProtocolDialog.Binder.class);
    }

    @UiField
    VerticalPanel protocolsPanel;

    @UiField
    Label descriptionNotNullLabel;

    private final Presenter presenter;
    private List<ProtocolType> protocolTypes;
    private List<String> protocolsList;
    private List<ProtocolDetail> selectedProtocolTypes;
    private DialogCallback<List<ProtocolDetail>> callback;
    private HashMap<String,TextBox> protocolDescriptions;

    public AddOptionalProtocolDialog(Presenter presenter, DialogCallback<List<ProtocolDetail>> callback) {
        this.presenter = presenter;
        this.callback = callback;

        loadProtocolTypes();

        setModal(true);
        setGlassEnabled(true);
        setText("New Optional Protocol");

        setWidget(AddOptionalProtocolDialog.Binder.BINDER.createAndBindUi(this));

        protocolsList = new ArrayList<>();
        selectedProtocolTypes = new ArrayList<>();
        protocolDescriptions = new HashMap<>();


        /*protocolsList.add("growth protocol");
        protocolsList.add("nucleic acid extraction protocol");
        protocolsList.add("nucleic acid library construction protocol");
        protocolsList.add("nucleic acid sequencing protocol");

        protocolsList.add("sample collection protocol");
        protocolsList.add("treatment protocol");
        protocolsList.add("normalization data transformation protocol");*/

        /*Label optionalProtocolsLabel = new Label("Optional Protocols");
        optionalProtocolsLabel.addStyleName("optionalProtocolHeaderLabel");
        optionalProtocolsLabel.addStyleName("optionalProtocolHeader");

        protocolsPanel.add(optionalProtocolsLabel);*/

        /*for (String protocol:
                optionalProtocols) {
            TextBox textBox = new TextBox();

            textBox.setWidth("100%");
            textBox.setName(protocol+"description");

            protocolDescriptions.put(protocol,textBox);

            protocolsPanel.add(new Label(protocol));
            protocolsPanel.add(textBox);
        }*/

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

        /*for (ProtocolDetail detail:
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
*/

        for(Map.Entry<String,TextBox> entry: protocolDescriptions.entrySet() )
        {
            if(!isNullOrEmpty(entry.getValue().getValue()))
            {
                ProtocolDetail detail = new ProtocolDetail();
                if(protocolsList.contains(entry.getKey().toString()))
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
                new ReportingAsyncCallback<ArrayList<ProtocolType>>(ReportingAsyncCallback.FailureMessage.UNABLE_TO_LOAD_PROTOCOL_TYPES) {
                    @Override
                    public void onSuccess(ArrayList<ProtocolType> types) {
                        showProtocolTypes(types);
                    }
                });
    }

    private void showProtocolTypes(List<ProtocolType> types) {
        protocolTypes = new ArrayList<>(types);
        selectedProtocolTypes.clear();
        protocolsList.clear();
        for (ProtocolType type : types)
        {
            protocolsList.add(type.getTerm().getLabel());
        }

        for (String protocol:
                protocolsList) {

            PlaceholderTextBox textBox = new PlaceholderTextBox();

            textBox.setWidth("100%");
            textBox.setName(protocol+"description");
            textBox.setPlaceholder("Protocol Description");


            protocolDescriptions.put(protocol,textBox);

            Label protocolLabel = new Label(protocol);
            protocolLabel.addStyleName("protocolLabel");

            protocolsPanel.add(protocolLabel);
            protocolsPanel.add(textBox);
        }
/*        for (ProtocolType type : types) {
            ProtocolDetail detail = new ProtocolDetail();
            if (mandatoryProtocols.contains(type.getTerm().getLabel())) {
                detail.setProtocolType(type);

                selectedProtocolTypes.add(detail);
            }
        }*/
    }

    public interface Presenter {
        void getProtocolTypes(AsyncCallback<ArrayList<ProtocolType>> callback);
    }
}

