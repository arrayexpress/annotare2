/*
 * Copyright 2009-2012 European Molecular Biology Laboratory
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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget;

import com.google.gwt.event.logical.shared.*;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DisclosurePanel;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.ItemSelectionEvent;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.ItemSelectionEventHandler;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.RecordChangeEventHandler;

/**
 * @author Olga Melnichuk
 */
public class DisclosureListItem extends Composite {

    private final DisclosurePanel panel = new DisclosurePanel();

    private final DisclosurePanelHeader header = new DisclosurePanelHeader();

    public DisclosureListItem(DisclosurePanelContent content) {
        panel.setWidth("100%");
        panel.addStyleName("app-IdfListItem");
        panel.setHeader(header);
        initWidget(panel);

        panel.addOpenHandler(new OpenHandler<DisclosurePanel>() {
            @Override
            public void onOpen(OpenEvent<DisclosurePanel> event) {
                header.setExpanded();
            }
        });

        panel.addCloseHandler(new CloseHandler<DisclosurePanel>() {
            @Override
            public void onClose(CloseEvent<DisclosurePanel> event) {
                header.setCollapsed();
            }
        });

        header.addItemSelectionHandler(new ItemSelectionEventHandler() {
            @Override
            public void onSelect(boolean selected) {
                fireEvent(new ItemSelectionEvent(selected));
            }
        });

        setContent(content);
    }

    public void setContent(DisclosurePanelContent w) {
        w.addContentChangeHandler(new RecordChangeEventHandler() {
            @Override
            public void onRecordChange(String value) {
                header.setTitle(value);
            }
        });
        panel.setContent(w);
    }

    public void addItemSelectionHandler(ItemSelectionEventHandler handler) {
        addHandler(handler, ItemSelectionEvent.TYPE);
    }

    public boolean isSelected() {
        return header.isSelected();
    }
}
