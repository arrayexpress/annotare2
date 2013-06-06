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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget;

import com.google.gwt.event.logical.shared.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.DialogBox;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.PrintingProtocolDto;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.DialogCloseEvent;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.DialogCloseHandler;

import java.util.List;

/**
 * @author Olga Melnichuk
 */
@Deprecated
public class PrintingProtocolDialog extends DialogBox implements HasSelectionHandlers<PrintingProtocolDto> {

    public PrintingProtocolDialog(List<PrintingProtocolDto> protocols, String selected) {
        setText("Select Printing Protocol");
        setGlassEnabled(true);
        setModal(true);

        PrintingProtocolDialogContent content = new PrintingProtocolDialogContent(protocols, selected);
        content.addDialogCloseHandler(new DialogCloseHandler<PrintingProtocolDto>() {
            @Override
            public void onDialogClose(DialogCloseEvent<PrintingProtocolDto> event) {
                if (event.hasResult()) {
                    fireSelectionEvent(event.getTarget());
                }
                hide();
            }
        });
        setWidget(content);
        center();
    }

    @Override
    public HandlerRegistration addSelectionHandler(SelectionHandler<PrintingProtocolDto> handler) {
        return addHandler(handler, SelectionEvent.getType());
    }

    private void fireSelectionEvent(PrintingProtocolDto selection) {
        SelectionEvent.fire(this, selection);
    }
}
