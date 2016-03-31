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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ValidationResult;

import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class EditorLogBarViewImpl extends Composite implements EditorLogBarView {

    private final VerticalPanel panel;

    public EditorLogBarViewImpl() {
        panel = new VerticalPanel();
        panel.add(new Label("No validation results"));
        initWidget(panel);
    }

    @Override
    public void showValidationResult(ValidationResult result) {
        panel.clear();
        if (result.getFailures().isEmpty()) {
            if (result.getErrors().isEmpty()) {
                panel.add(new Label("Validation has been successful"));
            } else {
                panel.add(new HTML("Validation failed with " + result.getErrors().size() + " errors (for help fixing errors please go to <a href=\"http://www.ebi.ac.uk/fg/annotare/help/validate_exp.html\" target=\"_blank\">http://www.ebi.ac.uk/fgpt/annotare_help/validate_exp.html</a>):"));
                addAll(result.getErrors());
            }
        } else {
            panel.add(new HTML("There was a problem with validation software, please contact <a href=\"mailto:annotare@ebi.ac.uk\">annotare@ebi.ac.uk</a> for help"));
            //addAll(result.getFailures());
        }
    }

    private void addAll(List<String> list) {
        for (String item : list) {
            panel.add(new Label(item));
        }
    }
}
