package uk.ac.ebi.fg.annotare2.prototypes.editorapp.client;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.*;

/**
 * @author Olga Melnichuk
 */
public class TextBoxWithPlaceHolder extends Composite implements IsWidget, HasValue<String> {

    private FocusPanel panel;

    private Label placeHolder;

    private TextBox textBox;

    private String defaultText = "Enter some text";

    public TextBoxWithPlaceHolder() {
        textBox = new TextBox();
        placeHolder = new Label(defaultText);
        panel = new FocusPanel();
        panel.setStyleName("my-TextBoxWithPlaceHolder");
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
                textBox.setFocus(true);
            }
        });

        textBox.addBlurHandler(new BlurHandler() {
            public void onBlur(BlurEvent event) {
                String v = textBox.getValue();
                if (v.isEmpty()) {
                    v = defaultText;
                }
                setPlaceHolderValue(v);
                panel.remove(textBox);
                panel.add(placeHolder);
            }
        });

        initWidget(panel);
    }

    public void setPlaceHolder(String defaultText) {
        String v = placeHolder.getText();
        String prevDefaultText = this.defaultText;
        this.defaultText = defaultText;
        if (v.equals(prevDefaultText)) {
            setPlaceHolderValue(defaultText);
        }
    }

    public String getValue() {
        return textBox.getValue();
    }

    public void setValue(String value) {
        textBox.setValue(value);
        setPlaceHolderValue(value);
    }

    public void setValue(String value, boolean fireEvents) {
        textBox.setValue(value, fireEvents);
        setPlaceHolderValue(value);
    }

    private void setPlaceHolderValue(String value) {
        placeHolder.setText(value);
        if (defaultText.equals(value)) {
            placeHolder.addStyleName("empty");
        } else {
            placeHolder.removeStyleName("empty");
        }
    }

    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> stringValueChangeHandler) {
        return textBox.addValueChangeHandler(stringValueChangeHandler);
    }

    @Override
    public void setWidth(String width) {
        super.setWidth(width);
        textBox.setWidth(width);
        placeHolder.setWidth(width);
    }
}
