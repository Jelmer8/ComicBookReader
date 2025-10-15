package xyz.jelmer8;


import xyz.jelmer8.controllers.Controller;
import xyz.jelmer8.models.BookModel;
import xyz.jelmer8.views.ComicBookView;
import xyz.jelmer8.views.View;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Frame;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;


public class Main {

    /** The default size of the views. */
    private static final Dimension DEFAULTVIEWSIZE = new Dimension(1400, 800);

    /**
     * Main method of the application.
     * @param args The arguments of the application.
     */
    public static void main(final String[] args) {
        // Create the frame
        Frame frame = new Frame();
        frame.setTitle("Comic Book Reader - Jelmer de Jong SE2B");
        frame.setSize(DEFAULTVIEWSIZE);
        // Create card layout for switching between views
        CardLayout cardLayout = new CardLayout();
        frame.setLayout(cardLayout);


        // Create the modelSet, views and controller
        HashMap<String, BookModel> modelMap = new HashMap<>();
        View mainMenuView = new View(DEFAULTVIEWSIZE);
        ComicBookView comicBookView = new ComicBookView(DEFAULTVIEWSIZE);

        new Controller(modelMap, mainMenuView, comicBookView, cardLayout);
        // The controller must have a copy of the model and view,
        // and does not need to be stored in a variable.


        frame.add(mainMenuView);
        frame.add(comicBookView);
        frame.setVisible(true);

        // Add a component listener to resize the views when the frame is resized
        frame.addComponentListener(new ComponentAdapter() {
            public void componentResized(final ComponentEvent componentEvent) {
                mainMenuView.setSize(frame.getSize());
                comicBookView.setSize(frame.getSize());
            }
        });

        // Add a window listener to close the frame when the window is closed
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(final WindowEvent we) {
                frame.dispose();
            }
        });
    }
}
