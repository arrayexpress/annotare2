package uk.ac.ebi.fg.annotare2.prototypes.editorapp.client;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.ui.*;

/**
 * @author Olga Melnichuk
 */
public class TextBoxWithPlaceHolder extends Composite implements IsWidget {

    private FocusPanel panel;

    private Label placeHolder;

    private TextBox textBox;

    private String defaultText = "Enter some text";

    public TextBoxWithPlaceHolder() {
        textBox = new TextBox();
        placeHolder = new Label(defaultText);
        panel = new FocusPanel();
        panel.add(placeHolder);

        panel.addFocusHandler(new FocusHandler() {
            public void onFocus(FocusEvent event) {
                String v = placeHolder.getText();
                if (v.equals(defaultText)) {
                    v = "";
                }
                textBox.setValue(v);
                panel.remove(placeHolder);
                panel.add(textBox);
            }
        });

        panel.addBlurHandler(new BlurHandler() {
            public void onBlur(BlurEvent event) {
                String v = textBox.getValue();
                if (v.isEmpty()) {
                    v = defaultText;
                }
                placeHolder.setText(v);
                panel.remove(textBox);
                panel.add(placeHolder);
            }
        });

        initWidget(panel);
    }

    public void setPlaceHolder(String defaultText) {
        this.defaultText = defaultText;
    }

}
