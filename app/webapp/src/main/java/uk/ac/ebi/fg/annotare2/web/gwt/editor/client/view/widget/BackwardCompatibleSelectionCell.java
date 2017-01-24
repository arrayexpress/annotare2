/*
 * Copyright 2009-2016 European Molecular Biology Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BrowserEvents;

import java.util.List;

public class BackwardCompatibleSelectionCell<C> extends DynSelectionCell<C> {


    public BackwardCompatibleSelectionCell(List<C> options) {
        super(options);
    }

    @Override
    protected int getSelectedIndex(C value) {
        updateOptions(value);
        Integer index = indexForOption.get(value);
        if (null == index) {
            return indexForOption.get(optionsProvider.getDefault().getValue());
        } else {
            return index;
        }
    }

    public void updateOptions(final C value) {
        updateOptions();
        if (!indexForOption.containsKey(value)) {
            indexForOption.put(value, options.size());
            options.add(new Option<C>() {
                @Override
                public C getValue() {
                    return value;
                }

                @Override
                public String getText() {
                    return value.toString();
                }
            });
        }
    }

    @Override
    protected void updateOptions() {
        options.clear();
        indexForOption.clear();
        if (optionsProvider.getDefault()!=null) {
            addOption(optionsProvider.getDefault());
        }
        List<Option<C>> opts = optionsProvider.getOptions();
        for (Option<C> option : opts) {
            addOption(option);
        }
    }

    private void addOption(Option<C> option) {
        indexForOption.put(option.getValue(), options.size());
        options.add(option);
    }
}
