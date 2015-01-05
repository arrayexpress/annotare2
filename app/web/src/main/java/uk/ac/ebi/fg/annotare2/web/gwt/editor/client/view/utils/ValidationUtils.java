/*
 * Copyright 2009-2015 European Molecular Biology Laboratory
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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.utils;

import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.TextBox;

import static com.google.gwt.event.dom.client.KeyCodes.*;

/**
 * @author Olga Melnichuk
 */
public class ValidationUtils {

    private static class NumbersOnly implements KeyPressHandler {
        @Override
        public void onKeyPress(KeyPressEvent event) {
            switch (event.getNativeEvent().getKeyCode()) {
                case KEY_TAB:
                case KEY_BACKSPACE:
                case KEY_DELETE:
                case KEY_LEFT:
                case KEY_RIGHT:
                case KEY_UP:
                case KEY_DOWN:
                case KEY_END:
                case KEY_ENTER:
                case KEY_ESCAPE:
                case KEY_PAGEDOWN:
                case KEY_PAGEUP:
                case KEY_HOME:
                case KEY_SHIFT:
                case KEY_ALT:
                case KEY_CTRL:
                    return;
                default:
                    if (event.isAltKeyDown() || (event.isControlKeyDown() && (event.getCharCode() != 'v' && event.getCharCode() != 'V'))) {
                        return;
                    }
                    if (Character.isDigit(event.getCharCode())) {
                        return;
                    }
                    if (event.getSource() instanceof TextBox) {
                        ((TextBox) event.getSource()).cancelKey();
                    }
            }
        }
    }

    public static void integerValuesOnly(TextBox integerBox) {
        integerBox.addKeyPressHandler(new NumbersOnly());
    }
}
