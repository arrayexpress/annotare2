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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.arraydesign.header;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.datepicker.client.DateBox;
import uk.ac.ebi.fg.annotare2.magetab.table.Cell;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.ComboBox;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.RichTextToolbar;

import java.util.Date;
import java.util.List;

import static uk.ac.ebi.fg.annotare2.web.gwt.editor.client.EditorUtils.dateTimeFormat;
import static uk.ac.ebi.fg.annotare2.web.gwt.editor.client.EditorUtils.dateTimeFormatPlaceholder;

/**
 * @author Olga Melnichuk
 */
public class AdfGeneralInfoViewImpl extends Composite implements AdfGeneralInfoView {

    @UiField
    TextBox designName;

    @UiField
    TextBox designVersion;

    @UiField
    ComboBox technologyType;

    @UiField
    ComboBox substrateType;

    @UiField
    ComboBox surfaceType;

    @UiField
    ComboBox species;

    @UiField
    TextArea description;

    @UiField
    DateBox publicReleaseDate;

    @UiField
    TextBox printingProtocol;

    @UiField
    TextBox ppName;

    @UiField
    HTML ppDescrPreview;

    @UiField
    SimplePanel ppDescrEditorDiv;

    @UiField
    Image displayButton;

    private RichTextArea richTextArea;
    private RichTextToolbar richTextToolbar;
    private boolean inPreviewMode = false;
    private Presenter presenter;

    interface Binder extends UiBinder<HTMLPanel, AdfGeneralInfoViewImpl> {
        Binder BINDER = GWT.create(Binder.class);
    }

    public AdfGeneralInfoViewImpl() {
        initWidget(Binder.BINDER.createAndBindUi(this));

        DateBox.DefaultFormat format = new DateBox.DefaultFormat(dateTimeFormat());
        publicReleaseDate.setFormat(format);
        publicReleaseDate.getElement().setPropertyString("placeholder", dateTimeFormatPlaceholder());

        ppDescrPreview.setVisible(inPreviewMode);

        richTextArea = new RichTextArea();
        richTextArea.setSize("100%", "14em");
        richTextToolbar = new RichTextToolbar(richTextArea);
        richTextToolbar.setWidth("100%");

        // Add the components to a panel
        Grid grid = new Grid(2, 1);
        grid.setStyleName("app-RichTextArea");
        grid.setWidget(0, 0, richTextToolbar);
        grid.setWidget(1, 0, richTextArea);
        ppDescrEditorDiv.setWidget(grid);

        displayButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (inPreviewMode) {
                    displayButton.getElement().getParentElement().removeClassName("clicked");
                } else {
                    ppDescrPreview.setHTML(richTextArea.getHTML());
                    ppDescrPreview.setWidth(ppDescrEditorDiv.getOffsetWidth() + "px");
                    ppDescrPreview.setHeight(ppDescrEditorDiv.getOffsetHeight() + "px");
                    displayButton.getElement().getParentElement().addClassName("clicked");
                }
                inPreviewMode = !inPreviewMode;
                ppDescrPreview.setVisible(inPreviewMode);
                ppDescrEditorDiv.setVisible(!inPreviewMode);
            }
        });
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setTechnologyTypes(List<String> types) {
        technologyType.setOptions(types);
    }

    @Override
    public void setSubstrateTypes(List<String> types) {
        substrateType.setOptions(types);
    }

    @Override
    public void setSurfaceTypes(List<String> types) {
        surfaceType.setOptions(types);
    }

    @Override
    public void setSpecies(List<String> species) {
        this.species.setOptions(species);
    }

    @Override
    public void setArrayDesignName(final Cell<String> cell) {
        attachCell(designName, cell);
    }

    @Override
    public void setVersion(Cell<String> cell) {
        attachCell(designVersion, cell);
    }

    @Override
    public void setPrintingProtocol(Cell<String> cell) {
        attachCell(printingProtocol, cell);
        //TODO update protocol name & description
    }

    @Override
    public void setTechnologyType(Cell<String> cell) {
        attachCell(technologyType, cell);
    }

    @Override
    public void setSurfaceType(Cell<String> cell) {
        attachCell(surfaceType, cell);
    }

    @Override
    public void setSubstrateType(Cell<String> cell) {
        attachCell(substrateType, cell);
    }

    @Override
    public void setDescription(Cell<String> cell) {
        attachCell(description, cell);
    }

    @Override
    public void setReleaseDate(Cell<Date> cell) {
        attachCell(publicReleaseDate, cell);
    }

    @Override
    public void setOrganism(Cell<String> cell) {
        attachCell(species, cell);
    }

    private <T> void attachCell(final HasValue<T> field, final Cell<T> cell) {
        field.setValue(cell.getValue(), false);
        field.addValueChangeHandler(new ValueChangeHandler<T>() {
            @Override
            public void onValueChange(ValueChangeEvent<T> event) {
                cell.setValue(event.getValue());
            }
        });
    }
}
