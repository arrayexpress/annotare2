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
        panel.addStyleName("app-log");
        initWidget(panel);
    }

    @Override
    public void showValidationResult(ValidationResult result) {
        panel.clear();
        if (result.getFailures().isEmpty()) {
            if (result.getErrors().isEmpty() && result.getWarnings().isEmpty()) {
                panel.add(new Label("Validation has been successful"));
            } else if(result.getErrors().isEmpty() && !result.getWarnings().isEmpty()) {
                panel.add(new Label("Validation has been successfull with " + result.getWarnings().size() + " warnings, please review:"));
                addAll(result.getWarnings());
            } else if(!result.getErrors().isEmpty() && result.getWarnings().isEmpty()) {
                panel.add(new HTML("Validation failed with " + result.getErrors().size() + " errors, please fix:"));
                addAll(result.getErrors());
            } else {
                panel.add(new HTML("Validation failed with " + result.getWarnings().size() + " warnings and " + result.getErrors().size() + " errors, please fix:"));
                addAll(result.getWarnings());
                addAll(result.getErrors());
            }
        } else {
            addAll(result.getFailures());
        }
    }

    private void addAll(List<String> list) {
        for (String item : list) {
            panel.add(new HTML(item));
        }
    }
}
