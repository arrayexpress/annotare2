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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.info;

import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.LeftNavigationView;

/**
 * @author Olga Melnichuk
 */
public enum ExpInfoSection implements LeftNavigationView.Section {
    GENERAL_INFO("General Information"),
    CONTACTS("Contact", "Enter the details of all persons that should appear as contacts for this experiment. There must be at least one 'submitter'."),
    PUBLICATIONS("Publications", "Enter the publication(s) using this experiment");

    private final String title;
    private final String helpText;

    ExpInfoSection(String title) {
        this.title = title;
        this.helpText = "";
    }

    ExpInfoSection(String title, String helpText) {
        this.title = title;
        this.helpText = helpText;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getKey() {
        return name();
    }

    @Override
    public String getHelpText() {
        return helpText;
    }
}
