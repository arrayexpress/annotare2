package uk.ac.ebi.fg.annotare2.prototypes.editorapp.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import uk.ac.ebi.fg.annotare2.prototypes.editorapp.client.data.SdrfData;

import java.util.*;

/**
 * @author Olga Melnichuk
 */
public class SdrfAssociationView extends Composite implements IsWidget {

    @UiField(provided = true)
    ListBox sourceBox;

    @UiField(provided = true)
    ListBox targetBox;

    @UiField(provided = true)
    ListBox suggestBox;

    @UiField
    Button deleteButton;

    @UiField
    Button addButton;

    @UiField
    Label title1;

    @UiField
    Label title2;

    @UiField
    Button directionButton;

    @UiField
    TextBox filterBox;

    @UiField
    Label title3;

    private Map<Integer, List<Integer>> helpMap = new HashMap<Integer, List<Integer>>();

    private Set<Integer> suggestions = new HashSet<Integer>();

    private Set<SdrfData.Pair<Integer>> associations = new HashSet<SdrfData.Pair<Integer>>();

    private SdrfData.Pair<SdrfSection> sectionPair;

    private boolean reverse;

    interface Binder extends UiBinder<Widget, SdrfAssociationView> {
    }

    public SdrfAssociationView(SdrfSection from, SdrfSection to) {
        associations.addAll(SdrfData.get().getAssociations(from, to));
        sectionPair = new SdrfData.Pair<SdrfSection>(from, to);

        sourceBox = new ListBox(true);
        targetBox = new ListBox(true);
        suggestBox = new ListBox(true);

        Binder uiBinder = GWT.create(Binder.class);
        Widget widget = uiBinder.createAndBindUi(this);
        initWidget(widget);

        deleteButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                List<SdrfRow> rows = getFirstRows();
                int index;
                while ((index = targetBox.getSelectedIndex()) >= 0) {
                    String value = targetBox.getItemText(index);
                    int key = parseInt(value);
                    for (int j = 0; j < sourceBox.getItemCount(); j++) {
                        if (sourceBox.isItemSelected(j)) {
                            removeFromMap(helpMap, j, key);
                            sourceBox.setItemText(j, sourceItem(rows.get(j).getName(), helpMap.get(j).size()));
                        }
                    }
                    targetBox.removeItem(index);
                }
            }
        });

        addButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                if (sourceBox.getSelectedIndex() < 0) {
                    return;
                }

                List<SdrfRow> rows = getFirstRows();
                for (int i = 0; i < suggestBox.getItemCount(); i++) {
                    if (!suggestBox.isItemSelected(i)) {
                        continue;
                    }
                    String value = suggestBox.getItemText(i);
                    int key = parseInt(value);
                    for (int j = 0; j < sourceBox.getItemCount(); j++) {
                        if (sourceBox.isItemSelected(j)) {
                            addToMap(helpMap, j, key);
                            sourceBox.setItemText(j, sourceItem(rows.get(j).getName(), helpMap.get(j).size()));
                        }
                    }
                    targetBox.addItem(value);
                    targetBox.setItemSelected(targetBox.getItemCount() - 1, true);

                }
            }
        });

        directionButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                switchDirection();
            }
        });

        sourceBox.addChangeHandler(new ChangeHandler() {
            public void onChange(ChangeEvent event) {
                targetBox.clear();
                for (String item : getTargetItems()) {
                    targetBox.addItem(item);
                }
            }
        });

        filterBox.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                String value = filterBox.getValue();
                if (value.length() == 0) {
                    fillSuggestionBox(suggestions);
                    return;
                }
                Set<Integer> newSuggestions = new HashSet<Integer>();
                List<SdrfRow> rows = getSecondRows();
                for (int i = 0; i < rows.size(); i++) {
                    SdrfRow row = rows.get(i);
                    if (row.getName().startsWith(value)) {
                        newSuggestions.add(i);
                    }
                }
                fillSuggestionBox(newSuggestions);
            }
        });
        setDirection(reverse);
    }

    private int parseInt(String value) {
        List<SdrfRow> list = getSecondRows();
        int key = -1;
        for (int i = 0; i < list.size(); i++) {
            SdrfRow row = list.get(i);
            if (row.getName().equals(value)) {
                key = i;
                break;
            }
        }
        return key;
    }

    private void switchDirection() {
        setDirection(!reverse);
    }

    private Set<String> getTargetItems() {
        Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        Set<Integer> selection = getSelection();
        for (Integer key : helpMap.keySet()) {
            if (!selection.contains(key)) {
                continue;
            }
            for (Integer conn : helpMap.get(key)) {
                Integer count = map.get(conn);
                if (count == null) {
                    map.put(conn, 1);
                } else {
                    map.put(conn, count + 1);
                }
            }
        }

        int n = selection.size();
        Set<String> result = new HashSet<String>();
        List<SdrfRow> rows = getSecondRows();
        for (Integer i : map.keySet()) {
            if (map.get(i) == n) {
                result.add(rows.get(i).getName());
            }
        }
        return result;
    }

    private Set<Integer> getSelection() {
        Set<Integer> sel = new HashSet<Integer>();
        for (int i = 0; i < sourceBox.getItemCount(); i++) {
            if (sourceBox.isItemSelected(i)) {
                sel.add(i);
            }
        }
        return sel;
    }

    private void setDirection(boolean isreversed) {
        SdrfSection from = sectionPair.first(isreversed);
        SdrfSection to = sectionPair.second(isreversed);
        title1.setText(from.getTitle());
        title2.setText(to.getTitle());
        title3.setText(to.getTitle());
        reverse = isreversed;

        suggestions = new HashSet<Integer>();
        helpMap = new HashMap<Integer, List<Integer>>();
        for (SdrfData.Pair<Integer> p : associations) {
            int first = p.first(reverse);
            int second = p.second(reverse);
            addToMap(helpMap, first, second);
            suggestions.add(second);
        }

        sourceBox.clear();
        List<SdrfRow> rows = from.getRows();
        for (int i = 0; i < rows.size(); i++) {
            List<Integer> list = helpMap.get(i);
            sourceBox.addItem(sourceItem(rows.get(i).getName(), list == null ? 0 : list.size()));
        }
        fillSuggestionBox(suggestions);
    }

    private String sourceItem(String name, int size) {
        return "( " + size + " ) " + name;
    }

    private void fillSuggestionBox(Set<Integer> suggestions) {
        List<SdrfRow> rows = getSecondRows();
        suggestBox.clear();
        List<Integer> suggestionsList = new ArrayList<Integer>(suggestions);
        Collections.sort(suggestionsList);
        for (Integer i : suggestionsList) {
            suggestBox.addItem(rows.get(i).getName());
        }
    }

    private void removeFromMap(Map<Integer, List<Integer>> helpMap, int first, int second) {
        List<Integer> list = helpMap.get(first);
        if (list == null) {
            return;
        }
        list.remove(Integer.valueOf(second));
    }

    private void addToMap(Map<Integer, List<Integer>> helpMap, int first, int second) {
        List<Integer> list = helpMap.get(first);
        if (list == null) {
            list = new ArrayList<Integer>();
            helpMap.put(first, list);
        }
        list.add(second);
    }

    private List<SdrfRow> getFirstRows() {
        return sectionPair.first(reverse).getRows();
    }

    private List<SdrfRow> getSecondRows() {
        return sectionPair.second(reverse).getRows();
    }
}
