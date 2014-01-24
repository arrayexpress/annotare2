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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.DialogBox;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.HasImportEventHandlers;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.ImportEventHandler;

/**
 * @author Olga Melnichuk
 */
public class ImportFileDialog extends DialogBox implements HasImportEventHandlers {

    private final ImportFileDialogContent content;

    public ImportFileDialog(String title) {
        setGlassEnabled(true);
        setText(title);

        content = new ImportFileDialogContent();
        setWidget(content);

        content.addCloseHandler(new CloseHandler<ImportFileDialogContent>() {
            @Override
            public void onClose(CloseEvent event) {
                hide();
            }
        });
        center();
    }

    @Override
    public HandlerRegistration addImportEventHandler(ImportEventHandler handler) {
        return content.addImportEventHandler(handler);
    }
}
