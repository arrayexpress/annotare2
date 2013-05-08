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

import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Label;

/**
 * @author Olga Melnichuk
 */
public class AutoSaveLabel extends Label {

    public void hide() {
        new FadeOutAnimation(getElement()).run(500);
    }

    public void show(String text) {
        getElement().getStyle().setOpacity(1.0);
        setText(text);
    }

    private static class FadeOutAnimation extends Animation {

        private final Element element;

        private FadeOutAnimation(Element element) {
            this.element = element;
        }

        @Override
        protected void onUpdate(double progress) {
            element.getStyle().setOpacity(1.0 - progress);
        }
    }
}
