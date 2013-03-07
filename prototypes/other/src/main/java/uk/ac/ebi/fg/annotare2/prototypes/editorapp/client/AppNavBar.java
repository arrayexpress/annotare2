package uk.ac.ebi.fg.annotare2.prototypes.editorapp.client;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class AppNavBar extends Composite implements IsWidget {

    private HorizontalPanel panel;
    private List<Command> commands = new ArrayList<Command>();
    private int selected = -1;

    public AppNavBar() {
        SimplePanel wrap = new SimplePanel();
        wrap.addStyleName("my-AppNavBar");
        wrap.setWidth("100%");
        panel = new HorizontalPanel();
        wrap.add(panel);
        initWidget(wrap);
    }

    public void addItem(String text, final Command command) {
        final int index = commands.size();

        final FocusPanel item = new FocusPanel(new Label(text));
        item.addStyleName("my-AppNavBarItem");
        item.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                select(index);
            }
        });
        item.addMouseOverHandler(new MouseOverHandler() {
            public void onMouseOver(MouseOverEvent event) {
                item.addStyleName("hovered");
            }
        });
        item.addMouseOutHandler(new MouseOutHandler() {
            public void onMouseOut(MouseOutEvent event) {
                item.removeStyleName("hovered");
            }
        });
        commands.add(command);
        panel.add(item);
    }

    public void select(int index) {
        if (selected >= 0) {
            panel.getWidget(selected).removeStyleName("selected");
        }
        Widget w = panel.getWidget(index);
        w.addStyleName("selected");
        selected = index;
        commands.get(index).execute();
    }
}
