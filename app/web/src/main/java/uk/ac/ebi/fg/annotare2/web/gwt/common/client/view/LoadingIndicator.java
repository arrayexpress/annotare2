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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or impl
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.web.gwt.common.client.view;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;

import static uk.ac.ebi.fg.annotare2.web.gwt.common.client.resources.CommonResources.COMMON_RESOURCES;

/**
 * @author Olga Melnichuk
 */
public class LoadingIndicator extends Composite {

    public LoadingIndicator() {
        COMMON_RESOURCES.styles().ensureInjected();
        SimplePanel panel = new SimplePanel();
        panel.addStyleName(COMMON_RESOURCES.styles().loadingIndicator());
        panel.addStyleName(COMMON_RESOURCES.styles().center());
        initWidget(panel);
    }
}
