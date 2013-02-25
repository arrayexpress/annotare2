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
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.datepicker.client.DateBox;
import uk.ac.ebi.fg.annotare2.magetab.table.Cell;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.UIPrintingProtocol;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.ComboBox;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.PrintingProtocolDialog;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.RichTextAreaExtended;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.RichTextToolbar;

import java.util.*;

import static uk.ac.ebi.fg.annotare2.web.gwt.editor.client.EditorUtils.dateTimeFormat;
import static uk.ac.ebi.fg.annotare2.web.gwt.editor.client.EditorUtils.dateTimeFormatPlaceholder;

/**
 * @author Olga Melnichuk
 */
public class AdfGeneralInfoViewImpl extends Composite implements AdfGeneralInfoView {

    interface Binder extends UiBinder<Widget, AdfGeneralInfoViewImpl> {
        Binder BINDER = GWT.create(Binder.class);
    }

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

    private RichTextAreaExtended richTextArea;
    private boolean inPreviewMode = false;
    private List<UIPrintingProtocol> printingProtocols = new ArrayList<UIPrintingProtocol>();
    private Set<String> protocolNames = new HashSet<String>();

    public AdfGeneralInfoViewImpl() {
        initWidget(Binder.BINDER.createAndBindUi(this));

        DateBox.DefaultFormat format = new DateBox.DefaultFormat(dateTimeFormat());
        publicReleaseDate.setFormat(format);
        publicReleaseDate.getElement().setPropertyString("placeholder", dateTimeFormatPlaceholder());

        printingProtocol.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                showPrintingProtocolDialog();
            }
        });

        ppDescrPreview.setVisible(inPreviewMode);

        richTextArea = new RichTextAreaExtended();
        richTextArea.setSize("100%", "14em");
        RichTextToolbar richTextToolbar = new RichTextToolbar(richTextArea);
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
                showPreview(!inPreviewMode);
            }
        });
    }

    private void showPrintingProtocolDialog() {
        PrintingProtocolDialog dialog = new PrintingProtocolDialog(printingProtocols, printingProtocol.getValue());
        dialog.addSelectionHandler(new SelectionHandler<UIPrintingProtocol>() {
            @Override
            public void onSelection(SelectionEvent<UIPrintingProtocol> event) {
                showPrintingProtocol(event.getSelectedItem(), true);
            }
        });
        dialog.show();
    }

    private void showPrintingProtocol(UIPrintingProtocol protocol, boolean fireEvent) {
        boolean exists = doesProtocolExist(protocol);
        printingProtocol.setValue(exists ? protocol.getName() : "NEW");

        if (protocol == null) {
            ppName.setValue("", fireEvent);
            richTextArea.setValue("", fireEvent);
        } else {
            ppName.setValue(protocol.getName(), fireEvent);
            richTextArea.setValue(protocol.getDescription(), fireEvent);
        }
        ppName.setEnabled(!exists);
        richTextArea.setEnabled(!exists);
        showPreview(false);
    }

    private void showPreview(boolean on) {
        if (on) {
            ppDescrPreview.setHTML(richTextArea.getHTML());
            ppDescrPreview.setWidth(ppDescrEditorDiv.getOffsetWidth() + "px");
            ppDescrPreview.setHeight(ppDescrEditorDiv.getOffsetHeight() + "px");
            displayButton.getElement().getParentElement().addClassName("clicked");
        } else {
            displayButton.getElement().getParentElement().removeClassName("clicked");
        }
        ppDescrPreview.setVisible(on);
        ppDescrEditorDiv.setVisible(!on);
        inPreviewMode = on;
    }

    private boolean doesProtocolExist(UIPrintingProtocol target) {
        return target != null && protocolNames.contains(target.getName());
    }

    @Override
    public void setPrintingProtocols(List<UIPrintingProtocol> protocols) {
        for (UIPrintingProtocol protocol : protocols) {
            protocolNames.add(protocol.getName());
            printingProtocols.add(protocol);
        }
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
    public void setPrintingProtocol(final Cell<String> cell) {
        showPrintingProtocol(UIPrintingProtocol.unsqueeeeze(cell.getValue()), false);
        ValueChangeHandler<String> handler = new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                cell.setValue(
                        new UIPrintingProtocol(ppName.getValue(), richTextArea.getHTML()).squeeeeze());
            }
        };
        ppName.addValueChangeHandler(handler);
        richTextArea.addValueChangeHandler(handler);
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
