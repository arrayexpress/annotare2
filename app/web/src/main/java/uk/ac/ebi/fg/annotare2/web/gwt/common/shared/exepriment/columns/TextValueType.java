package uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.columns;

/**
 * @author Olga Melnichuk
 */
public class TextValueType implements ColumnValueType {

    @Override
    public void visit(Visitor visitor) {
        visitor.onTextValueType(this);
    }
}
