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

package uk.ac.ebi.fg.annotare2.web.gwt.common.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

public interface CommonResources extends ClientBundle {

    public static final CommonResources COMMON_RESOURCES = GWT.create(CommonResources.class);

    public interface CommonBundledStyles extends CssResource {

        @ClassName("loading-indicator")
        String loadingIndicator();

        @ClassName("center")
        String center();
    }

    @Source("../../public/images/loading64.gif")
    public ImageResource largeLoader();

    @Source("../../public/CommonApp-bundled.css")
    public CommonBundledStyles styles();
}
