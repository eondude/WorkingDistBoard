package gui;

/*
 * Name: Walid Moustafa
 * Student ID: 563080
 * Subject: COMP90015 - Distributed Systems
 * Assignment: Assignment 2 - Distributed Whiteboard
 * Project: com.walidmoustafa.board.App
 * File: com.walidmoustafa.board.gui.UserListRenderer.java
*/


import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

//Sets color to differentiate between admin and users

class UserListRenderer extends DefaultListCellRenderer {

    private static final long serialVersionUID = 1L;

    @Override
    public Component getListCellRendererComponent(JList<?> list,
                                                  Object value, int index, boolean isSelected, boolean cellHasFocus) {

        String currentUser = value.toString();
        setText(currentUser);

        Color background;
        Color foreground;

        if (currentUser.equals(WhiteBoard.userID)) {
            background = Color.WHITE;
            foreground = Color.BLUE;
        } else if (currentUser.equals(WhiteBoard.adminID)) {
            background = Color.WHITE;
            foreground = Color.RED;
        } else {
            if (WhiteBoard.isAdmin) {
                if (isSelected) {
                    background = Color.RED;
                    foreground = Color.WHITE;
                } else {
                    background = Color.WHITE;
                    foreground = Color.BLACK;
                }
            } else {
                background = Color.WHITE;
                foreground = Color.BLACK;
            }
        }

        setBackground(background);
        setForeground(foreground);

        return this;

    }
}

