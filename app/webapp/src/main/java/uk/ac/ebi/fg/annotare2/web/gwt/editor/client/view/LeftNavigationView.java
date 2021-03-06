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

import com.google.gwt.user.client.ui.IsWidget;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ArrayDesignRef;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ExperimentSettings;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.settings.ExperimentSettingsPanel;

import java.util.List;

/**
 * @author Olga Melnichuk
 */
public interface LeftNavigationView extends IsWidget {

    void setPresenter(Presenter presenter);

    void setSections(Section... sections);

    void setSections(List<? extends Section> sections);

    void setSelected(Section section);

    void setExperimentSettings(ExperimentSettings settings);

    void setArrayDesignList(List<ArrayDesignRef> arrayDesigns);

    public interface Presenter extends ExperimentSettingsPanel.Presenter {

        void navigateTo(Section section);
    }

    public interface Section {

        String getTitle();

        String getKey();

        String getHelpText(); // Technically it's the title attribute but that property has already been taken
    }
}
