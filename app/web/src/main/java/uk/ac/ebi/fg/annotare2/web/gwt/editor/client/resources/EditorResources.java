/*
 * Copyright 2009-2012 European Molecular Biology Laboratory
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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author Olga Melnichuk
 */
public interface EditorResources extends ClientBundle {

    public static final EditorResources EDITOR_RESOURCES = GWT.create(EditorResources.class);

    @Source("../../public/images/expand-icon.png")
    public ImageResource expandIcon();

    @Source("../../public/images/collapse-icon.png")
    public ImageResource collapseIcon();

    @Source("../../public/images/dropdown-icon.png")
    public ImageResource dropDownIcon();

    @Source("../../public/images/trash-icon.png")
    public ImageResource trashIcon();

    @Source("../../public/images/plus-icon.png")
    public ImageResource plusIcon();

    @Source("../../public/images/display-icon.png")
    public ImageResource displayIcon();

    @Source("../../public/images/upload-button2.png")
    public ImageResource uploadFilesButton();

    @Source("../../public/images/loading64.gif")
    public ImageResource loadingIndicator();

    @Source("../../public/editor-bundled.css")
    public EditorBundledStyles editorStyles();
}
