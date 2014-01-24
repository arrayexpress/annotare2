/*
 * Copyright 2009-2014 European Molecular Biology Laboratory
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public abstract class AsyncOptionProvider {

    private final List<OptionDisplay> displays = new ArrayList<OptionDisplay>();

    public void addOptionDisplay(OptionDisplay display) {
        displays.add(display);
    }

    public void update() {
        update(new Callback() {
            @Override
            public void setOptions(Collection<String> options) {
                for (OptionDisplay display : displays) {
                    display.updateOptions(options);
                }
            }
        });
    }

    public abstract void update(Callback callback);

    public interface OptionDisplay {
        void updateOptions(Collection<String> options);
    }

    public interface Callback {
        void setOptions(Collection<String> options);
    }
}
