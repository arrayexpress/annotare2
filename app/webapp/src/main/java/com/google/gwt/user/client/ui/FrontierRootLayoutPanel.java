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

package com.google.gwt.user.client.ui;

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;

public class FrontierRootLayoutPanel extends LayoutPanel {

    private static FrontierRootLayoutPanel singleton;

    public static FrontierRootLayoutPanel get() {
        if (singleton == null) {
            singleton = new FrontierRootLayoutPanel();
            RootPanel.get("content").add(singleton);
        }
        return singleton;
    }

    private FrontierRootLayoutPanel() {
        Window.addResizeHandler(new ResizeHandler() {
            public void onResize(ResizeEvent event) {
                FrontierRootLayoutPanel.this.onResize();
            }
        });
    }

    @Override
    protected void onLoad() {
        getLayout().onAttach();
        getLayout().fillParent();
    }
}