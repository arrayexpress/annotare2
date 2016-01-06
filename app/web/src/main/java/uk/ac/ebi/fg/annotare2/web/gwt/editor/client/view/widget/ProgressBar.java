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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Olga Melnichuk
 */
public class ProgressBar extends Composite {

    interface Binder extends UiBinder<Widget, ProgressBar> {
        Binder BINDER = GWT.create(Binder.class);
    }

    @UiField
    DivElement progress;

    public ProgressBar() {
        initWidget(Binder.BINDER.createAndBindUi(this));
        setProgress(0);
    }

    public void setProgress(double v) {
        if (v > 1 || v < 0) {
            throw new IndexOutOfBoundsException("Value is out of bounds [0, 1]: " + v);
        }
        progress.getStyle().setWidth(v*100, Style.Unit.PCT);
    }
}
