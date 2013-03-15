package uk.ac.ebi.fg.annotare2.prototypes.editorapp.client;


import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.ui.MenuItem;

import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class SdrfCellOptions extends Composite implements IsWidget {

    private static final String SCROLLABLE_HEIGHT = "150px";

    @UiField
    SimpleLayoutPanel center;

    @UiField
    Anchor editLink;

    private final OptionList optionList;

    interface Binder extends UiBinder<Widget, SdrfCellOptions> {
    }

    public SdrfCellOptions(List<String> options) {
        Binder uiBinder = GWT.create(Binder.class);
        Widget widget = uiBinder.createAndBindUi(this);
        initWidget(widget);

        optionList = new OptionList();
        ScrollPanel scrollPanel = new ScrollPanel(optionList);
        center.add(scrollPanel);

        setOptions(options);
    }

    private void setOptions(List<String> options) {
        if (options.size() > 6) {
            getWidget().setHeight(SCROLLABLE_HEIGHT);
        }
        for(final String o : options) {
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
