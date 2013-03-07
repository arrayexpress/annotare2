package uk.ac.ebi.fg.annotare2.prototypes.editorapp.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;

/**
 * @author Olga Melnichuk
 */
public class SubmissionCreateDialogContent2 extends Composite implements IsWidget, HasCloseHandlers<SubmissionCreateDialogContent2> {

    @UiField
    ListBox designBox;

    @UiField
    ListBox speciesBox;

    @UiField
    ListBox templateBox;

    @UiField
    Button cancelButton;

    @UiField
    Button noneButton;

    @UiField
    Button okButton;

    @UiField
    HTML preview;

    public HandlerRegistration addCloseHandler(CloseHandler<SubmissionCreateDialogContent2> handler) {
        return addHandler(handler, CloseEvent.getType());
    }

    interface Binder extends UiBinder<Widget, SubmissionCreateDialogContent2> {
    }

    static String odd = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Etiam ac sapien tincidunt quam adipiscing porta. Morbi vel enim eget justo interdum congue. Fusce est tortor, sagittis vitae laoreet eget, adipiscing in nulla. Donec ultrices sollicitudin dolor sed varius. Maecenas tristique feugiat tellus non pulvinar. Vestibulum auctor tellus at sem pulvinar lobortis. Nulla porta pellentesque egestas. Sed sit amet leo urna. Aliquam pellentesque nisi vitae elit pharetra tristique. Pellentesque in dolor eget lorem lobortis posuere. Quisque vel bibendum felis. Suspendisse in tellus tortor, non tristique sapien. Curabitur viverra eros vel lectus pretium malesuada nec eu eros. Duis faucibus, velit at euismod feugiat, diam felis feugiat quam, aliquet dictum dolor tellus consectetur ipsum. Integer cursus, mauris ut dapibus dignissim, magna diam sollicitudin purus, sed imperdiet massa nibh ac ipsum. In tempus metus quis augue adipiscing vel rutrum diam tincidunt. ";
    static String even = "Nam ac massa a velit congue interdum. Nam quam magna, consequat nec posuere vitae, ullamcorper et arcu. Curabitur tempus pulvinar facilisis. In ut urna mi. Suspendisse condimentum urna ac diam ornare ut ornare ipsum euismod. Aenean scelerisque, urna egestas scelerisque tincidunt, est quam volutpat quam, in tincidunt risus augue ac sem. Nunc eleifend erat quis erat aliquet condimentum. Quisque bibendum massa at risus ullamcorper semper. Integer semper ligula eget nisi varius a posuere leo tempus. In vitae nibh at diam rutrum placerat faucibus suscipit purus. ";

    public SubmissionCreateDialogContent2() {
        Binder uiBinder = GWT.create(Binder.class);
        Widget widget = uiBinder.createAndBindUi(this);
        initWidget(widget);

        okButton.addClickHandler(closeHandler());
        cancelButton.addClickHandler(closeHandler());
        noneButton.addClickHandler(closeHandler());

        templateBox.addChangeHandler(new ChangeHandler() {
            public void onChange(ChangeEvent event) {
                showTemplate(templateBox.getSelectedIndex());
            }
        });

        for (String d : SubmissionCreateDialogContent.designs) {
            designBox.addItem(d);
        }

        for (String s : SubmissionCreateDialogContent.species) {
            speciesBox.addItem(s);
        }

        for (int i = 0; i < 25; i++) {
            templateBox.addItem("Template " + i);
        }

        templateBox.setItemSelected(0, true);
        DomEvent.fireNativeEvent(Document.get().createChangeEvent(), templateBox);
    }

    private void showTemplate(int selectedIndex) {
        preview.setHTML(selectedIndex %2 == 0 ? even : odd);
    }

    private ClickHandler closeHandler() {
        final SubmissionCreateDialogContent2 that = this;
        return new ClickHandler() {
            public void onClick(ClickEvent event) {
                CloseEvent.fire(that, that);
            }
        };
    }
}
