package uk.ac.ebi.fg.annotare2.prototypes.editorapp.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author Olga Melnichuk
 */
public interface EditorClientBundle extends ClientBundle {

    public static final EditorClientBundle INSTANCE =  GWT.create(EditorClientBundle.class);

    @Source("../public/more-styles.css")
    MoreStyles moreStyles();

    @Source("../public/draggable2.png")
    ImageResource draggableIcon();

    @Source("../public/triangle.png")
    ImageResource triangleIcon();

}
