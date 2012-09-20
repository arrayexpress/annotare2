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

import com.google.gwt.user.client.ui.DialogBox;
import com.google.web.bindery.event.shared.HandlerRegistration;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.AsyncEventFinishListener;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.FinishEventHandler;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.ProceedEventHandler;

/**
 * @author Olga Melnichuk
 */
public class ImportFileDialog extends DialogBox {

    private final ImportFileDialogContent content;

    public ImportFileDialog(String title) {
        setGlassEnabled(true);

        setText(title);

        content = new ImportFileDialogContent();
        setWidget(content);

        content.addImportFinishEventHandler(new FinishEventHandler() {
            @Override
            public void onFinish() {
                hide();
            }
        });

        center();
    }

    public HandlerRegistration addImportFileDialogHandler(final Handler handler) {
        return content.addImportProceedEventHandler(new ProceedEventHandler() {
            public void onProceed(AsyncEventFinishListener listener) {
                handler.onImport(content.getFileName(), listener);
            }
        });
    }

    public static interface Handler {
        void onImport(String fileName, AsyncEventFinishListener listener);
    }

}
