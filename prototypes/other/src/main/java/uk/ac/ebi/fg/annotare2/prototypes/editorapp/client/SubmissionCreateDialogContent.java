package uk.ac.ebi.fg.annotare2.prototypes.editorapp.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * @author Olga Melnichuk
 */
public class SubmissionCreateDialogContent extends Composite implements IsWidget, HasCloseHandlers<SubmissionCreateDialogContent> {

    @UiField
    ListBox designBox;

    @UiField
    ListBox speciesBox;

    @UiField
    ListBox templateBox;

    @UiField
    Button okButton;

    @UiField
    Button noneButton;

    @UiField
    Button cancelButton;

    public HandlerRegistration addCloseHandler(CloseHandler<SubmissionCreateDialogContent> handler) {
        return addHandler(handler, CloseEvent.getType());
    }

    interface Binder extends UiBinder<Widget, SubmissionCreateDialogContent> {
    }

    static List<String> designs = asList(
            "Any",
            "Disease State",
            "CGH",
            "Behavior",
            "Cell Type Comparison",
            "Development or Differentiation",
            "Generic Characteristics",
            "Generic Modification",
            "Organism Part Comparison",
            "Sex",
            "Strain or Line",
            "etc.");

    static List<String> species = asList(
            "Any",
            "Homo sapiens",
            "Mus Musculus",
            "Rattus Norvegicus",
            "etc."
    );

    public SubmissionCreateDialogContent() {
        Binder uiBinder = GWT.create(Binder.class);
        Widget widget = uiBinder.createAndBindUi(this);
        initWidget(widget);


        for (String d : designs) {
            designBox.addItem(d);
        }

        for (String s : species) {
            speciesBox.addItem(s);
        }

        for (int i = 0; i < 25; i++) {
            templateBox.addItem("Template " + i);
        }

        okButton.addClickHandler(closeHandler());
        cancelButton.addClickHandler(closeHandler());
        noneButton.addClickHandler(closeHandler());
    }

    private ClickHandler closeHandler() {
        final SubmissionCreateDialogContent that = this;
        return new ClickHandler() {
            public void onClick(ClickEvent event) {
                CloseEvent.fire(that, that);
            }
        };
    }
}
