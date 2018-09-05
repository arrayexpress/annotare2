package uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment;

import com.google.gwt.user.client.rpc.IsSerializable;
import uk.ac.ebi.fg.annotare2.submission.model.ExtractAttribute;
import uk.ac.ebi.fg.annotare2.submission.model.SingleCellExtractAttribute;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.HasIdentity;

import java.util.HashMap;
import java.util.Map;

public class SingleCellExtractAttributesRow implements IsSerializable, HasIdentity {
    private int id;
    private String name;
    private Map<SingleCellExtractAttribute, String> values;

    SingleCellExtractAttributesRow() {
        /*used by GWT serialization only */
    }

    public SingleCellExtractAttributesRow(int id, String name, Map<SingleCellExtractAttribute, String> values) {
        this.id = id;
        this.name = name;
        this.values = new HashMap<>(values);
    }

    @Override
    public Object getIdentity() {
        return id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue(SingleCellExtractAttribute attr) {
        return values.get(attr);
    }

    public void setValue(String value, SingleCellExtractAttribute attr) {
        values.put(attr, value);
    }

    public Map<SingleCellExtractAttribute, String> getValues() {
        return new HashMap<>(values);
    }

    public SingleCellExtractAttributesRow copy() {
        return new SingleCellExtractAttributesRow(id, name, values);
    }
}
