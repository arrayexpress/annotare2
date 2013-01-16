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

import com.google.gwt.user.client.ui.DialogBox;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.idf.UITermSource;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.CloseEventHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class TermSourceTemplatesDialog extends DialogBox {

    private final TermSourceTemplatesDialogContent content;

    public TermSourceTemplatesDialog(List<UITermSource> templates) {
        setText("Add Term Source(s)");
        setGlassEnabled(true);
        setModal(true);

        content = new TermSourceTemplatesDialogContent(templates);
        content.addCloseHandler(new CloseEventHandler() {
            @Override
            public void onClose() {
                hide();
            }
        });

        setWidget(content);

        center();
    }

    public boolean isCancelled() {
        return false; //TODO
    }

    public ArrayList<UITermSource> getSelection() {
        return new ArrayList<UITermSource>(); //TODO
    }
}
