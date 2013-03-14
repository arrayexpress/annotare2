package uk.ac.ebi.fg.annotare2.prototypes.editorapp.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.ui.*;

import java.util.*;

/**
 * @author Olga Melnichuk
 */
public class SdrfAssociationView extends Composite implements IsWidget {

    private static final int N = 100;

    private static Set<Pair> assosiations = new HashSet<Pair>();

    static {
        while (assosiations.size() < 2 * N) {
            assosiations.add(new Pair(Random.nextInt(N), Random.nextInt(N)));
        }
    }

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

    private boolean reverse;

    private Map<Integer, List<Integer>> helpMap = new HashMap<Integer, List<Integer>>();

    private Set<Integer> suggestions = new HashSet<Integer>();

    interface Binder extends UiBinder<Widget, SdrfAssociationView> {
    }

    public SdrfAssociationView(SdrfSection from, SdrfSection to) {
        sourceBox = new ListBox(true);
        targetBox = new ListBox(true);
        suggestBox = new ListBox(true);

        Binder uiBinder = GWT.create(Binder.class);
        Widget widget = uiBinder.createAndBindUi(this);
        initWidget(widget);

        deleteButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                String prefix = getFirstPrefix();
                int index;
                while ((index = targetBox.getSelectedIndex()) >= 0) {
                    String value = targetBox.getItemText(index);
                    int key = parseInt(value);
                    for (int j = 0; j < sourceBox.getItemCount(); j++) {
                        if (sourceBox.isItemSelected(j)) {
                            removeFromMap(helpMap, j, key);
                            sourceBox.setItemText(j, sourceItem(prefix, j, helpMap.get(j).size()));
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

                String prefix = getFirstPrefix();
                for (int i = 0; i < suggestBox.getItemCount(); i++) {
                    if (suggestBox.isItemSelected(i)) {
                        String value = suggestBox.getItemText(i);
                        int key = parseInt(value);
                        for (int j = 0; j < sourceBox.getItemCount(); j++) {
                            if (sourceBox.isItemSelected(j)) {
                                addToMap(helpMap, j, key);
                                sourceBox.setItemText(j, sourceItem(prefix, j, helpMap.get(j).size()));
                            }
                        }
                        targetBox.addItem(value);
                        targetBox.setItemSelected(targetBox.getItemCount() - 1, true);
                    }
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
                for (Integer i : suggestions) {
                    if (Integer.toString(i).startsWith(value)) {
                        newSuggestions.add(i);
                    }
                }
                fillSuggestionBox(newSuggestions);
            }
        });
        setDirection(from.getTitle(), to.getTitle(), false);
    }

    private static int parseInt(String value) {
        int k = value.lastIndexOf(" ");
        return Integer.parseInt(value.substring(k + 1, value.length()));
    }

    private void switchDirection() {
        setDirection(title2.getText(), title1.getText(), !reverse);
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
        String prefix = title2.getText();
        for (Integer i : map.keySet()) {
            if (map.get(i) == n) {
                result.add(prefix + " " + i);
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

    private void setDirection(String from, String to, boolean isreversed) {
        title1.setText(from);
        title2.setText(to);
        title3.setText(to);
        reverse = isreversed;

        suggestions = new HashSet<Integer>();
        helpMap = new HashMap<Integer, List<Integer>>();
        for (Pair p : assosiations) {
            int first = p.first(reverse);
            int second = p.second(reverse);
            addToMap(helpMap, first, second);
            suggestions.add(second);
        }

        sourceBox.clear();
        for (int i = 0; i < N; i++) {
            List<Integer> list = helpMap.get(i);
            sourceBox.addItem(sourceItem(from, i, list == null ? 0 : list.size()));
        }

        fillSuggestionBox(suggestions);
    }

    private String sourceItem(String prefix, int key, int size) {
        return "( " + size + " ) " + prefix + " " + key;
    }

    private void fillSuggestionBox(Set<Integer> suggestions) {
        String prefix = getSecondPrefix();
        suggestBox.clear();
        List<Integer> suggestionsList = new ArrayList<Integer>(suggestions);
        Collections.sort(suggestionsList);
        for (Integer i : suggestionsList) {
            suggestBox.addItem(prefix + " " + i);
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

    private String getFirstPrefix() {
        return title1.getText();
    }

    private String getSecondPrefix() {
        return title2.getText();
    }

    private static class Pair {
        private final int first;
        private final int second;

        private Pair(int first, int second) {
            this.first = first;
            this.second = second;
        }

        public int first(boolean reverse) {
            return reverse ? second : first;
        }

        public int second(boolean reverse) {
            return reverse ? first : second;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Pair pair = (Pair) o;

            if (first != pair.first) return false;
            if (second != pair.second) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = first;
            result = 31 * result + second;
            return result;
        }
    }
}
