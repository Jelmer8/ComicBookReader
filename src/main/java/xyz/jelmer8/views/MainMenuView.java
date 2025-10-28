package xyz.jelmer8.views;

import java.awt.Panel;
import java.awt.FlowLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.ScrollPane;
import java.awt.Label;
import java.awt.TextField;
import java.awt.Button;
import java.awt.Color;

import java.awt.event.ActionListener;

import java.util.List;


public class MainMenuView extends Panel {

    /** Enum to represent the type of message in the message label. */
    public enum MessageType {
        /** The message is a (green) info message. */
        INFO,
        /** The message is a (red) error message. */
        ERROR,
        /** The message is empty. Used to clear the label */
        EMPTY
    }

    /** The panel where the paths of the books are displayed. */
    private final Panel pathsPanel;

    /** The button to load the books. */
    private final Button button;

    /** The text field to input the path of the books. */
    private final TextField pathTextField;

    /** The label to display messages. */
    private final Label messageLabel;

    /**
     * Constructor for the View class.
     * @param viewSize The size of the view panel
     */
    public MainMenuView(final Dimension viewSize) {
        setSize(viewSize);
        setLayout(new FlowLayout());

        pathsPanel = new Panel();
        pathsPanel.setLayout(new GridBagLayout());

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.add(pathsPanel);

        messageLabel = new Label("");
        messageLabel.setPreferredSize(new Dimension(1400, 50));
        messageLabel.setAlignment(Label.CENTER);
        add(messageLabel);

        pathTextField = new TextField("target/books");
        add(pathTextField);

        button = new Button("Load Books");
        button.setPreferredSize(new Dimension(100, 25));
        add(button);

        Label spacer = new Label("");
        spacer.setPreferredSize(new Dimension(1400, 75));
        add(spacer);

        scrollPane.setPreferredSize(new Dimension(1200, 500));
        add(scrollPane);


        setVisible(true);
    }


    /**
     * Method to get the path from the pathTextArea.
     * @return {String} path from the pathTextArea
     */
    public String getPathTextAreaText() {
        return pathTextField.getText();
    }


    /**
     * Method to set the books in the pathsPanel.
     * @param labels List<Label> of the labels to display
     * @param buttons List<Button> of the buttons to display
     */
    public void setBooksInPanel(final List<Label> labels,
                                final List<Button> buttons) {

        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;

        pathsPanel.removeAll();

        for (int i = 0; i < labels.size(); i++) {
            pathsPanel.add(labels.get(i));
            pathsPanel.add(buttons.get(i), gridBagConstraints);
        }

        // Validate the panel to make sure the changes are visible
        pathsPanel.validate();
    }


    /**
     * Method to set the info message in the messageLabel.
     * @param type the MessageType of the message, either INFO, ERROR or EMPTY
     * @param message the String to display
     */
    public void setInfoMessage(final MessageType type, final String message) {
        messageLabel.setText(message);
        switch (type) {
            case INFO:
                messageLabel.setBackground(Color.GREEN);
                break;
            case ERROR:
                messageLabel.setBackground(Color.RED);
                break;
            case EMPTY:
                messageLabel.setBackground(Color.WHITE);
                break;
        }
    }


    /**
     * Add an action listener to the button.
     * @param listener the ActionListener to add to the button
     */
    public void addButtonActionListener(final ActionListener listener) {
        button.addActionListener(listener);
    }
}
