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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.design;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import uk.ac.ebi.fg.annotare2.configmodel.OntologyTerm;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.dto.EfoGraphDto;

import java.util.ArrayList;
import java.util.List;

import static com.google.gwt.safehtml.shared.SafeHtmlUtils.fromString;

/**
 * @author Olga Melnichuk
 */
public class ProtocolCreateDialog extends DialogBox {

    interface Binder extends UiBinder<Widget, ProtocolCreateDialog> {
        Binder BINDER = GWT.create(Binder.class);
    }

    @UiField
    Button cancelButton;

    @UiField
    Button okButton;

    @UiField
    Tree protocolTypeTree;

    @UiField
    ListBox protocolList;

    final Presenter presenter;

    public ProtocolCreateDialog(Presenter presenter) {
        this.presenter = presenter;

        setModal(true);
        setGlassEnabled(true);
        setText("New Protocol");

        setWidget(Binder.BINDER.createAndBindUi(this));

        protocolTypeTree.addSelectionHandler(new SelectionHandler<TreeItem>() {
            @Override
            public void onSelection(SelectionEvent<TreeItem> event) {
                loadProtocols((OntologyTerm)event.getSelectedItem().getUserObject());
            }
        });

        center();
        loadProtocolTypes();
        showProtocols(new ArrayList<OntologyTerm>());
    }

    @UiHandler("cancelButton")
    void cancelClicked(ClickEvent event) {
        hide();
    }

    private void loadProtocolTypes() {
        if (presenter == null) {
            return;
        }
        presenter.getProtocolTypes(new AsyncCallback<EfoGraphDto>() {
            @Override
            public void onFailure(Throwable caught) {
                Window.alert("Server error; can't load list of protocol types");
            }

            @Override
            public void onSuccess(EfoGraphDto graph) {
                for(EfoGraphDto.Node node : graph.getRoots()) {
                    showProtocolTypes(protocolTypeTree, node);
                }
            }
        });
    }

    private void showProtocolTypes(HasTreeItems treeNode, EfoGraphDto.Node graphNode) {
        OntologyTerm term = graphNode.getTerm();
        TreeItem treeItem = treeNode.addItem(fromString(term.getLabel()));
        treeItem.setUserObject(term);

        for (EfoGraphDto.Node child : graphNode.getChildren()) {
            showProtocolTypes(treeItem, child);
        }
    }

    private void loadProtocols(OntologyTerm term) {
        presenter.getProtocols(term, new AsyncCallback<List<OntologyTerm>>(){
            @Override
            public void onFailure(Throwable caught) {
                Window.alert("Server error; can't load list of protocols");
            }

            @Override
            public void onSuccess(List<OntologyTerm> result) {
                showProtocols(result);
            }
        });
    }

    private void showProtocols(List<OntologyTerm> protocols) {
        protocolList.clear();
        for(OntologyTerm term : protocols) {
            protocolList.addItem(term.getLabel(), term.getAccession());
        }
        if (protocols.isEmpty()) {
            protocolList.addItem("No Protocols");
            protocolList.getElement().getElementsByTagName("option").getItem(0).setAttribute("disabled", "disabled");
        }
    }

    public static interface Presenter {

        void getProtocolTypes(AsyncCallback<EfoGraphDto> callback);

        void getProtocols(OntologyTerm protocolType, AsyncCallback<List<OntologyTerm>> callback);
    }
}
