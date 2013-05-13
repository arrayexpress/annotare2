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

import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.DialogBox;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.idf.UITermSource;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.DialogCloseEvent;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.DialogCloseHandler;

import java.util.List;

/**
 * @author Olga Melnichuk
 */
@Deprecated
public class TermSourceTemplatesDialog extends DialogBox implements HasSelectionHandlers<List<UITermSource>> {

    public TermSourceTemplatesDialog(List<UITermSource> templates) {
        setText("Add Term Source(s)");
        setGlassEnabled(true);
        setModal(true);

        TermSourceTemplatesDialogContent content = new TermSourceTemplatesDialogContent(templates);
        content.addDialogCloseHandler(new DialogCloseHandler<List<UITermSource>>() {
            @Override
            public void onDialogClose(DialogCloseEvent<List<UITermSource>> event) {
                if (event.hasResult()) {
                    fireSelectionEvent(event.getTarget());
                }
                hide();
            }
        });
        setWidget(content);
        center();
    }

    private void fireSelectionEvent(List<UITermSource> target) {
        SelectionEvent.fire(this, target);
    }

    @Override
    public HandlerRegistration addSelectionHandler(SelectionHandler<List<UITermSource>> handler) {
        return addHandler(handler, SelectionEvent.getType());
    }
}
