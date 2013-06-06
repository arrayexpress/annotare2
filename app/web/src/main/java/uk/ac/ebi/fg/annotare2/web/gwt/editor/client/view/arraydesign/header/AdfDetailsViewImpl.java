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
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.datepicker.client.DateBox;
import uk.ac.ebi.fg.annotare2.magetab.table.Cell;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.PrintingProtocolDto;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.dto.EfoTermDto;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.*;

import java.util.*;

import static java.lang.Integer.toString;
import static java.lang.Integer.toString;
import static uk.ac.ebi.fg.annotare2.web.gwt.editor.client.EditorUtils.dateTimeFormat;
import static uk.ac.ebi.fg.annotare2.web.gwt.editor.client.EditorUtils.dateTimeFormatPlaceholder;

/**
 * @author Olga Melnichuk
 */
public class AdfDetailsViewImpl extends Composite implements AdfDetailsView {

    interface Binder extends UiBinder<Widget, AdfDetailsViewImpl> {
        Binder BINDER = GWT.create(Binder.class);
    }

    @UiField
    TextBox designName;

    @UiField
    TextBox designVersion;

    @UiField(provided = true)
    SuggestBox species;

    @UiField
    TextArea description;

    @UiField
    DateBox publicReleaseDate;

    @UiField
    ListBox printingProtocolList;

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
    private Map<Integer, PrintingProtocolDto> printingProtocols = new HashMap<Integer, PrintingProtocolDto>();
    private Presenter presenter;

    public AdfDetailsViewImpl() {
        species = new SuggestBox(new EfoSuggestOracle(new SuggestService<EfoTermDto>() {
            @Override
            public void suggest(String query, int limit, AsyncCallback<List<EfoTermDto>> callback) {
                presenter.getOrganisms(query, limit, callback);
            }
        }));

        initWidget(Binder.BINDER.createAndBindUi(this));

        DateBox.DefaultFormat format = new DateBox.DefaultFormat(dateTimeFormat());
        publicReleaseDate.setFormat(format);
        publicReleaseDate.getElement().setPropertyString("placeholder", dateTimeFormatPlaceholder());

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

        setPrintingProtocols(new ArrayList<PrintingProtocolDto>());
    }

    private void showPrintingProtocol(PrintingProtocolDto protocol, boolean fireEvent) {
        boolean editable = !protocol.hasId();

        ppName.setValue(protocol.getName(), fireEvent);
        richTextArea.setValue(unescapeHtml(protocol.getDescription()), fireEvent);

        ppName.setEnabled(editable);
        richTextArea.setEnabled(editable);
        showPreview(false);
    }

    private String unescapeHtml(String text) {
        return new HTML(text).getText();
    }

    private String escapeHtml(String html) {
        return SafeHtmlUtils.fromString(html).asString();
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

    @UiHandler("printingProtocolList")
    void onProtocolSelect(ChangeEvent event) {
        int index = printingProtocolList.getSelectedIndex();
        int id = Integer.parseInt(printingProtocolList.getValue(index));
        showPrintingProtocol(printingProtocols.get(id), true);
    }

    @Override
    public void setPrintingProtocols(List<PrintingProtocolDto> protocols) {
        printingProtocols = new HashMap<Integer, PrintingProtocolDto>();
        printingProtocolList.clear();
        for (PrintingProtocolDto protocol : protocols) {
            addProtocol(protocol);
        }
    }

    private void addProtocol(PrintingProtocolDto protocol) {
        String name = protocol.hasId() ? protocol.getName() : "Other";
        printingProtocols.put(protocol.getId(), protocol);
        printingProtocolList.addItem(name, Integer.toString(protocol.getId()));
    }

    private void setProtocolSelected(PrintingProtocolDto protocol) {
        for (int i = 0; i < printingProtocolList.getItemCount(); i++) {
            if (protocol.getId() == Integer.parseInt(printingProtocolList.getValue(i))) {
                printingProtocolList.setItemSelected(i, true);
                DomEvent.fireNativeEvent(Document.get().createChangeEvent(), printingProtocolList);
                break;
            }
        }
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
        PrintingProtocolDto protocol = PrintingProtocolDto.unsqueeeeze(cell.getValue());
        showPrintingProtocol(protocol, false);
        ValueChangeHandler<String> handler = new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                PrintingProtocolDto protocol = new PrintingProtocolDto(ppName.getValue(), escapeHtml(richTextArea.getHTML()));
                cell.setValue(protocol.squeeeeze());
            }
        };
        ppName.addValueChangeHandler(handler);
        richTextArea.addValueChangeHandler(handler);

        addProtocol(protocol);
        setProtocolSelected(protocol);
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

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
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
