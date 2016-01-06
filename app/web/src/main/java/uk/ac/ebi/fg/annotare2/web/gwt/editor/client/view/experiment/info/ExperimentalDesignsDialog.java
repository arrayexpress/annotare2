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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.info;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.SelectionChangeEvent;
import uk.ac.ebi.fg.annotare2.submission.model.OntologyTerm;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.view.DialogCallback;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.OntologyTermGroup;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.SelectableLabel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.google.gwt.safehtml.shared.SafeHtmlUtils.fromSafeConstant;

/**
 * @author Olga Melnichuk
 */
public class ExperimentalDesignsDialog extends DialogBox {

    interface Binder extends UiBinder<Widget, ExperimentalDesignsDialog> {
        Binder BINDER = GWT.create(Binder.class);
    }

    @UiField
    Button cancelButton;

    @UiField
    Button okButton;

    @UiField
    SimpleLayoutPanel contentPanel;

    //private Tooltip tooltip;
    private Set<OntologyTerm> selection = new HashSet<OntologyTerm>();
    private DialogCallback<List<OntologyTerm>> callback;

    public ExperimentalDesignsDialog(List<OntologyTermGroup> groups, DialogCallback<List<OntologyTerm>> callback) {
        this.callback = callback;

        setModal(true);
        setGlassEnabled(true);
        setText("Experimental Designs");

        //tooltip = new Tooltip();
        //tooltip.setWidth("200px");

        setWidget(Binder.BINDER.createAndBindUi(this));
        contentPanel.add(createContent(groups));

        center();
    }

    @UiHandler("okButton")
    void okButtonClicked(ClickEvent event) {
        hide();
        if (null != callback) {
            callback.onOkay(getSelection());
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

    private Widget createContent(List<OntologyTermGroup> groups) {
        StackLayoutPanel stackPanel = new StackLayoutPanel(Style.Unit.PX);
        stackPanel.setWidth("100%");
        for (OntologyTermGroup group : groups) {
            stackPanel.add(createSectionContent(group), fromSafeConstant(group.getName()), 25);
        }
        return stackPanel;
    }

    private Widget createSectionContent(OntologyTermGroup group) {
        VerticalPanel panel = new VerticalPanel();
        panel.setWidth("100%");
        panel.setSpacing(4);

        for (OntologyTerm term : group.getTerms()) {
            final SelectableLabel<OntologyTerm> label = new SelectableLabel<OntologyTerm>(term.getLabel(), term);
            label.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
                @Override
                public void onSelectionChange(SelectionChangeEvent event) {
                    updateSelection(label.getValue(), label.isSelected());
                }
            });
            //String definition = group.getDefinition(term);
            //if (definition != null && !definition.isEmpty()) {
            //    tooltip.attach(label.info(), definition);
            //}
            panel.add(label);
        }

        ScrollPanel scrollPanel = new ScrollPanel();
        scrollPanel.add(panel);
        return scrollPanel;
    }

    private void updateSelection(OntologyTerm term, boolean selected) {
        if (selected) {
            selection.add(term);
        } else if (selection.contains(term)) {
            selection.remove(term);
        }
    }

    private List<OntologyTerm> getSelection() {
        return new ArrayList<OntologyTerm>(selection);
    }
}
