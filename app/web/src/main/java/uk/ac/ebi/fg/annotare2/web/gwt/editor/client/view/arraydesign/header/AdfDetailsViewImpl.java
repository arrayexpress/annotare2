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
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.datepicker.client.DateBox;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.PrintingProtocolDto;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.arraydesign.ArrayDesignDetailsDto;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.dto.EfoTermDto;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.EfoSuggestOracle;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.RichTextAreaExtended;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.RichTextToolbar;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.SuggestService;

import java.util.*;

import static java.lang.Integer.parseInt;
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
    TextBox protocolName;

    @UiField
    HTML ppDescrPreview;

    @UiField
    SimplePanel ppDescrEditorDiv;

    @UiField
    Image displayButton;

    private RichTextAreaExtended protocolDescription;
    private boolean inPreviewMode = false;
    private Map<Integer, PrintingProtocolDto> printingProtocols = new HashMap<Integer, PrintingProtocolDto>();
    private Presenter presenter;

    private EfoTermDto organism;

    public AdfDetailsViewImpl() {
        species = new SuggestBox(new EfoSuggestOracle(new SuggestService<EfoTermDto>() {
            @Override
            public void suggest(String query, int limit, AsyncCallback<List<EfoTermDto>> callback) {
                presenter.getOrganisms(query, limit, callback);
            }
        }));
        species.addSelectionHandler(new SelectionHandler<SuggestOracle.Suggestion>() {
            @Override
            public void onSelection(SelectionEvent<SuggestOracle.Suggestion> event) {
                EfoSuggestOracle.EfoTermSuggestion suggestion = (EfoSuggestOracle.EfoTermSuggestion) event.getSelectedItem();
                setOrganism(suggestion.getTerm());
                save();
            }
        });

        initWidget(Binder.BINDER.createAndBindUi(this));

        DateBox.DefaultFormat format = new DateBox.DefaultFormat(dateTimeFormat());
        publicReleaseDate.setFormat(format);
        publicReleaseDate.getElement().setPropertyString("placeholder", dateTimeFormatPlaceholder());

        ppDescrPreview.setVisible(inPreviewMode);

        protocolDescription = new RichTextAreaExtended();
        protocolDescription.setSize("100%", "14em");
        RichTextToolbar richTextToolbar = new RichTextToolbar(protocolDescription);
        richTextToolbar.setWidth("100%");
        protocolDescription.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                savePrintingProtocol();
            }
        });

        // Add the components to a panel
        Grid grid = new Grid(2, 1);
        grid.setStyleName("app-RichTextArea");
        grid.setWidget(0, 0, richTextToolbar);
        grid.setWidget(1, 0, protocolDescription);
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

        protocolName.setValue(protocol.getName(), fireEvent);
        protocolDescription.setValue(unescapeHtml(protocol.getDescription()), fireEvent);

        protocolName.setEnabled(editable);
        protocolDescription.setEnabled(editable);
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
            ppDescrPreview.setHTML(protocolDescription.getHTML());
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
    void printingProtocolChanged(ChangeEvent event) {
        int index = printingProtocolList.getSelectedIndex();
        int id = parseInt(printingProtocolList.getValue(index));
        showPrintingProtocol(printingProtocols.get(id), true);
    }

    @UiHandler("designName")
    void nameChanged(ChangeEvent event) {
        save();
    }

    @UiHandler("description")
    void descriptionChanged(ChangeEvent event) {
        save();
    }

    @UiHandler("designVersion")
    void designVersionChanged(ChangeEvent event) {
        save();
    }

    @UiHandler("species")
    void speciesChanged(ValueChangeEvent<String> event) {
        save();
    }

    @UiHandler("publicReleaseDate")
    void publicReleaseDateChanged(ValueChangeEvent<Date> event) {
        save();
    }

    @UiHandler("protocolName")
    void protocolNameChanged(ChangeEvent event) {
        savePrintingProtocol();
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
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

    private void setProtocolSelected(PrintingProtocolDto protocol, boolean fireEvents) {
        for (int i = 0; i < printingProtocolList.getItemCount(); i++) {
            if (protocol.getId() == parseInt(printingProtocolList.getValue(i))) {
                printingProtocolList.setItemSelected(i, true);
                if (fireEvents) {
                    DomEvent.fireNativeEvent(Document.get().createChangeEvent(), printingProtocolList);
                }
                break;
            }
        }
    }

    @Override
    public void setDetails(ArrayDesignDetailsDto details) {
        if (details == null) {
            return;
        }
        designName.setValue(details.getArrayDesignName());
        designVersion.setValue(details.getVersion());
        description.setValue(details.getDescription());
        publicReleaseDate.setValue(details.getPublicReleaseDate());
        setOrganism(details.getOrganism());
        addProtocol(details.getOtherPrintingProtocol());

        PrintingProtocolDto protocol = printingProtocols.get(details.getPrintingProtocolId());
        setProtocolSelected(protocol, false);
        showPrintingProtocol(protocol, false);
    }

    private void setOrganism(EfoTermDto term) {
        if (term != null) {
            organism = term;
            species.setValue(term.getLabel());
        }
    }

    private int getSelectedProtocolId() {
        int index = printingProtocolList.getSelectedIndex();
        return parseInt(printingProtocolList.getValue(index));
    }

    private PrintingProtocolDto getOtherProtocol() {
        return printingProtocols.get(0);
    }

    private ArrayDesignDetailsDto getResult() {
        return new ArrayDesignDetailsDto(
                designName.getValue(),
                description.getValue(),
                designVersion.getValue(),
                organism,
                publicReleaseDate.getValue(),
                getSelectedProtocolId(),
                getOtherProtocol());
    }

    private void savePrintingProtocol() {
        PrintingProtocolDto selectedProtocol = printingProtocols.get(getSelectedProtocolId());
        if (!selectedProtocol.hasId()) {
            printingProtocols.put(selectedProtocol.getId(), new PrintingProtocolDto(
                    protocolName.getValue(),
                    protocolDescription.getValue()
            ));
        }
        save();
    }

    private void save() {
        presenter.updateDetails(getResult());
    }
}
