package uk.ac.ebi.fg.annotare2.prototypes.editorapp.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;

/**
 * @author Olga Melnichuk
 */
public class IdfView extends Composite implements IsWidget {

    @UiField
    TabLayoutPanel tabPanel;

    interface Binder extends UiBinder<DockLayoutPanel, IdfView> {
    }

    public IdfView() {
        Binder uiBinder = GWT.create(Binder.class);
        Widget widget = uiBinder.createAndBindUi(this);
        initWidget(widget);

        tabPanel.add(new IdfContentView(), "Edit");
        tabPanel.add(new Label("Read-only table here"), "Preview");
    }
}
