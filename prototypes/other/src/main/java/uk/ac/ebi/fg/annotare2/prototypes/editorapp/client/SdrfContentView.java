package uk.ac.ebi.fg.annotare2.prototypes.editorapp.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import uk.ac.ebi.fg.annotare2.prototypes.editorapp.client.event.SelectionEvent;
import uk.ac.ebi.fg.annotare2.prototypes.editorapp.client.event.SelectionEventHandler;

/**
 * @author Olga Melnichuk
 */
public class SdrfContentView extends Composite implements IsWidget {

    @UiField
    SdrfNavigationPanel navigation;

    @UiField
    SimpleLayoutPanel content;

    interface Binder extends UiBinder<DockLayoutPanel, SdrfContentView> {
    }

    public SdrfContentView() {
        Binder uiBinder = GWT.create(Binder.class);
        Widget widget = uiBinder.createAndBindUi(this);
        initWidget(widget);

        navigation.addSelectionHandler(new SelectionEventHandler<SdrfNavigationPanel.Item>() {
            public void onSelection(SelectionEvent<SdrfNavigationPanel.Item> event) {
                SdrfNavigationPanel.Item sel = event.getSelection();
                Widget w = sel.isPair() ? new SdrfAssociationView(sel.getSection1(), sel.getSection2()) :
                        new SdrfSectionView(sel.getSection1(), sel.isFirst());
                content.setWidget(w);
            }
        });
    }
}
