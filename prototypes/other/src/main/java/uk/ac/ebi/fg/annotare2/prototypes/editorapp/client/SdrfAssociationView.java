package uk.ac.ebi.fg.annotare2.prototypes.editorapp.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.ui.*;

/**
 * @author Olga Melnichuk
 */
public class SdrfAssociationView extends Composite implements IsWidget {

    private static final int N = 100;

    private static final String[] words = new String[]{"Sample 1", "Sample 2", "Sample 3"};

    @UiField
    Label title;

    @UiField
    ListBox sourceBox;

    @UiField(provided = true)
    ListBox targetBox;

    @UiField
    Button advancedAdd;

    @UiField
    Button deleteButton;

    @UiField(provided = true)
    SuggestBox suggestBox;

    @UiField
    Button addButton;

    interface Binder extends UiBinder<Widget, SdrfAssociationView> {
    }

    public SdrfAssociationView(String section1, String section2) {
        MultiWordSuggestOracle oracle = new MultiWordSuggestOracle();
        for (String word : words) {
            oracle.add(word);
        }

        targetBox = new ListBox(true);
        suggestBox = new SuggestBox(oracle);

        Binder uiBinder = GWT.create(Binder.class);
        Widget widget = uiBinder.createAndBindUi(this);
        initWidget(widget);

        title.setText(section1 + " <--> " + section2);

        deleteButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                int index;
                while ((index = targetBox.getSelectedIndex()) >= 0) {
                    targetBox.removeItem(index);
                }
            }
        });

        addButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                String value = suggestBox.getValue();
                if (value.length() > 0) {
                    targetBox.addItem(value);
                    targetBox.setItemSelected(targetBox.getItemCount() - 1, true);
                }
            }
        });

        for (int i = 0; i < N; i++) {
            sourceBox.addItem("Source " + i);
        }

        sourceBox.addChangeHandler(new ChangeHandler() {
            public void onChange(ChangeEvent event) {
                targetBox.clear();
                for (int i = 0; i < N / 2; i++) {
                    targetBox.addItem("Sample " + Random.nextInt());
                }
            }
        });
    }
}
