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
import uk.ac.ebi.fg.annotare2.submission.model.Protocol;
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
    private List<Protocol> selectedProtocolTypes;
    private DialogCallback<List<Protocol>> callback;
    private HashMap<String,TextArea> protocolDescriptions;

    public AddOptionalProtocolDialog(Presenter presenter, DialogCallback<List<Protocol>> callback) {
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

        for(Map.Entry<String,TextArea> entry: protocolDescriptions.entrySet() )
        {
            if(!isNullOrEmpty(entry.getValue().getValue()))
            {
                Protocol protocol = new Protocol(1);
                if(protocolsList.contains(entry.getKey().toString()))
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

        AddProtocolDialog.addProtocolsToPanel(protocolsList, protocolsPanel, protocolDescriptions);
    }

    public interface Presenter {
        void getProtocolTypes(AsyncCallback<ArrayList<ProtocolType>> callback);
    }
}

