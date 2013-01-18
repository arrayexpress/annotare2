package uk.ac.ebi.fg.annotare2.web.server.rpc;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.VocabularyService;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.idf.UITerm;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.idf.UITermSource;

import java.util.ArrayList;

/**
 * @author Olga Melnichuk
 */
public class VocabularyServiceImpl extends RemoteServiceServlet implements VocabularyService {

    //TODO add EFO Service here

    @Override
    public ArrayList<UITerm> getExperimentalDesigns() {
        UITermSource ts = new UITermSource("efo", "", "", "aa");
        ArrayList<UITerm> list = new ArrayList<UITerm>();
        list.add(new UITerm("case control design", "", ts, "biological variation design"));
        list.add(new UITerm("all pairs", "", ts, "methodological variation design"));
        list.add(new UITerm("array platform variation design", "", ts, "methodological variation design"));
        list.add(new UITerm("array specific design", "", ts, "methodological variation design"));
        return list;
    }
}
