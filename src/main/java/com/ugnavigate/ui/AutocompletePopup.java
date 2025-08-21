package com.ugnavigate.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.List;
import java.util.function.Consumer;

/**
 * Lightweight popup list for textfield autocomplete.
 */
public class AutocompletePopup {
    private final JPopupMenu popup = new JPopupMenu();
    private final JList<String> list = new JList<>();
    private Consumer<String> onChoose;

    public AutocompletePopup() {
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && onChoose != null && list.getSelectedValue() != null) {
                onChoose.accept(list.getSelectedValue());
            }
        });
        JScrollPane scroll = new JScrollPane(list);
        scroll.setPreferredSize(new Dimension(280, 180));
        popup.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        popup.add(scroll);
    }

    public void show(JTextField field, List<String> suggestions, Consumer<String> onChoose) {
        this.onChoose = onChoose;
        list.setListData(suggestions.toArray(new String[0]));
        System.out.println("Popup.show() CALLED with matches: " + suggestions);

//        try {
//            Rectangle r = field.getUI().getRootView(field).modelToView(field, field.getCaretPosition(), null).getBounds();
//            popup.show(field, 0, r.y + r.height);
//        } catch (Exception ex) {
//            popup.show(field, 0, field.getHeight());
//        }
//        try {
//            Rectangle r = field.modelToView(field.getCaretPosition());
//            popup.show(field, 0, r.y + r.height);
//        } catch (Exception ex) {
//            popup.show(field, 0, field.getHeight());
//        }

        try {
            Rectangle r = field.modelToView(field.getCaretPosition());
            popup.show(field, r.x, r.y + r.height);
        } catch (Exception ex) {
            popup.show(field, 0, field.getHeight());
        }


        list.clearSelection();
        list.requestFocusInWindow();
    }

    public void hide(JTextField field) {
        popup.setVisible(false);
    }

    public FocusAdapter getFocusHider(JTextField field) {
        return new FocusAdapter() {
            @Override public void focusLost(FocusEvent e) { hide(field); }
        };
    }
}
