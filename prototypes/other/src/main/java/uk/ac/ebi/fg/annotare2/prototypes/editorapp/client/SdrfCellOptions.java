package uk.ac.ebi.fg.annotare2.prototypes.editorapp.client;


import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.ui.MenuItem;
import uk.ac.ebi.fg.annotare2.prototypes.editorapp.client.event.HasSelectionHandlers;
import uk.ac.ebi.fg.annotare2.prototypes.editorapp.client.event.SelectionEvent;
import uk.ac.ebi.fg.annotare2.prototypes.editorapp.client.event.SelectionEventHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class SdrfCellOptions extends Composite implements IsWidget,
        HasSelectionHandlers<String> {

    @UiField
    SimplePanel center;

    @UiField
    Anchor editLink;

    @UiField
    Anchor createLink;

    private final OptionList optionList;

    private final ScrollPanel scrollPanel;

    private List<String> options = new ArrayList<String>();

    interface Binder extends UiBinder<Widget, SdrfCellOptions> {
    }

    public SdrfCellOptions(List<String> options) {
        Binder uiBinder = GWT.create(Binder.class);
        Widget widget = uiBinder.createAndBindUi(this);
        initWidget(widget);

        optionList = new OptionList();
        scrollPanel = new ScrollPanel(optionList);
        center.add(scrollPanel);

        this.options.addAll(options);
        setOptions(this.options);
    }

    @Override
    public HandlerRegistration addSelectionHandler(SelectionEventHandler<String> handler) {
        return  addHandler(handler, SelectionEvent.getType());
    }

    private void setOptions(List<String> options) {
        scrollPanel.setHeight(options.size() > 5 ? "160px" : "auto");
        optionList.clearItems();
        for (final String o : options) {
            optionList.addItem(new Option(o, true, new Scheduler.ScheduledCommand() {
                @Override
                public void execute() {
                    optionSelected(o);
                }
            }));
        }
    }

    private void optionSelected(String option) {
        //todo
    }

    public void filter(String value) {
        List<String> filtered;
        if (value.isEmpty()) {
            filtered = options;
        } else {
            filtered = new ArrayList<String>();
            for (String o : options) {
                if (o.startsWith(value)) {
                    filtered.add(o);
                }
            }
        }
        setOptions(filtered);
        showOptionCreateLink(value, filtered.isEmpty());
    }

    private void showOptionCreateLink(String value, boolean show) {
        createLink.setText(value + " (create)");
        createLink.setVisible(show);
    }

    private static class OptionList extends MenuBar {
        private OptionList() {
            super(true);
            setFocusOnHoverEnabled(false);
        }

        @Override
        protected com.google.gwt.user.client.ui.MenuItem getSelectedItem() {
            return super.getSelectedItem();
        }
    }

    private static class Option extends MenuItem {
        private String option;

        public Option(String option, boolean asHTML, Scheduler.ScheduledCommand command) {
            super(option, asHTML, command);
            DOM.setStyleAttribute(getElement(), "whiteSpace", "nowrap");
            this.option = option;
        }

        public String getValue() {
            return option;
        }
    }
}
