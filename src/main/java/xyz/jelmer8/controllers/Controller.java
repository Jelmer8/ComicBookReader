package xyz.jelmer8.controllers;

import xyz.jelmer8.models.BookModel;
import xyz.jelmer8.models.RarBookModel;
import xyz.jelmer8.models.ZipBookModel;
import xyz.jelmer8.views.ComicBookView;
import xyz.jelmer8.views.MainMenuView;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Label;
import java.awt.Button;

import java.awt.event.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;



public class Controller {

    /** The starting view, main menu. */
    private final MainMenuView mainMenuView;

    /** The view where the user can read the comic book. */
    private final ComicBookView comicBookView;

    /** The model that the controller will be working with. */
    private final HashMap<String, BookModel> bookModelMap;

    private String currentBookPath;

    /** The card layout of the GUI, used to control which screen is visible. */
    private final CardLayout mainCardLayout;


    /**
     * Constructor of the Controller class.
     * @param modelMap the set of models
     * @param view the starting view, main menu
     * @param comicBookView the comic book view
     * @param cardLayout the cardLayout of the frame
     */
    public Controller(HashMap<String, BookModel> modelMap, final MainMenuView view,
                      final ComicBookView comicBookView,
                      final CardLayout cardLayout) {
        this.bookModelMap = modelMap;
        this.mainMenuView = view;
        this.comicBookView = comicBookView;
        this.mainCardLayout = cardLayout;

        // Button listener for the 'load books' button on the main view
        mainMenuView.addButtonActionListener((ActionEvent e) -> {
            try {
                loadBooks();
            } catch (Exception ex) {
                mainMenuView.setInfoMessage(MainMenuView.MessageType.ERROR,
                        "Error: " + ex);
            }
        });

        // Button listener for the 'stop reading' button on the comic book view
        // And the 'previous page' and 'next page' buttons
        comicBookView.setButtonListeners(
                _ -> {
                    comicBookView.clearImage();
                    mainCardLayout.previous(mainMenuView.getParent());
                },
                _ -> comicBookView.setComicBookImage(
                        bookModelMap.get(currentBookPath).getPreviousBookImage()),
                _ -> comicBookView.setComicBookImage(
                        bookModelMap.get(currentBookPath).getNextBookImage())
        );

        // Listening for mouse drag events on the comic book view.
        // Used for panning the comic book
        comicBookView.setPanelListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(final MouseEvent e) {
                comicBookView.panComicBookImage(e.getXOnScreen(),
                        e.getYOnScreen());
            }
        }, new MouseAdapter() {
            @Override
            public void mousePressed(final MouseEvent e) {
                comicBookView.setMousePressed(e.getXOnScreen(), e.getYOnScreen());
            }
        });

        // Add a component listener to resize the comic book image when the view is resized
        comicBookView.addComponentListener(new ComponentAdapter() {
            public void componentResized(final ComponentEvent componentEvent) {
                comicBookView.resizeComicBookImage();
            }
        });
    }


    /**
     * This method will load all the books from the path that the user
     * has inputted in the text area. It will display the books in the
     * panel of the view.
     */
    public void loadBooks() {
        try {
            List<Path> books = getBooksFromPath();

            // If no books are found at the provided path, show error message
            if (books.isEmpty()) {
                mainMenuView.setInfoMessage(MainMenuView.MessageType.ERROR,
                        "No books found in specified path.");
                return;
            }


            // Make list of labels and buttons to update the view with
            List<Label> labels = generateLabelsForBook(books);
            List<Button> buttons = generateButtonsForBook(books);


            // Update the view with the books
            mainMenuView.setBooksInPanel(labels, buttons);

            // Show success message
            mainMenuView.setInfoMessage(MainMenuView.MessageType.INFO,
                    "Books loaded: " + books.size());
        } catch (Exception e) {
            mainMenuView.setInfoMessage(MainMenuView.MessageType.ERROR, "Error: " + e);
        }
    }


    /**
     * Make UI Label components for the loaded books.
     * @param books List<Path> of all the loaded books
     * @return List<Label>
     */
    private List<Label> generateLabelsForBook(final List<Path> books) {
        List<Label> labels = new ArrayList<>();

        for (Path book : books) {
            labels.add(new Label(book.getFileName().toString(),
                    Label.CENTER));
        }

        return labels;
    }


    /**
     * Make UI Button components for the loaded books.
     * @param books List<Path> of all the loaded books
     * @return List<Button>
     */
    private List<Button> generateButtonsForBook(final List<Path> books) {
        List<Button> buttons = new ArrayList<>();

        for (Path book : books) {
            Button b = new Button("Load");
            b.setPreferredSize(new Dimension(75, 25));
            b.addActionListener((ActionEvent _) -> loadBook(book.toString()));

            buttons.add(b);
        }

        return buttons;
    }


    private void createBookModel(final String bookPath) {
        BookModel bookModel;

        // Determine the type of book and create the appropriate model
        if (bookPath.endsWith(".cbz") || bookPath.endsWith(".nhlcomic")) {
            bookModel = new ZipBookModel(bookPath);
        } else if (bookPath.endsWith(".cbr")) {
            bookModel = new RarBookModel(bookPath);
        } else {
            mainMenuView.setInfoMessage(MainMenuView.MessageType.ERROR,
                    "Error: Unsupported file extension");
            return; // If there's an error, return
        }

        try {
            bookModel.decompressBook();
        } catch (Exception ex) {
            mainMenuView.setInfoMessage(MainMenuView.MessageType.ERROR,
                    "Error: " + ex);
            return; // If there's an error, return
        }

        // Add the book model to the hashmap
        bookModelMap.put(bookPath, bookModel);
    }


    /**
     * This method will open a book from the path that the user has
     * clicked on in the panel.
     *
     * @param bookPath string containing the path to the book to be loaded
     */
    private void loadBook(final String bookPath) {
        currentBookPath = bookPath;

        // If there is no book model for the book, create one
        if (!bookModelMap.containsKey(bookPath)) {
            createBookModel(bookPath);
        }

        final BookModel bookModel = bookModelMap.get(bookPath);

        // Get the image of the book that was previously seen
        try {
            comicBookView.setComicBookImage(bookModel.getBookImage(-1));
        } catch (Exception e) {
            mainMenuView.setInfoMessage(MainMenuView.MessageType.ERROR,
                    "Error occurred while loading the book");
            return; // If there's an error, return
        }


        // Switch to the comic book view
        mainCardLayout.next(mainMenuView.getParent());
        mainMenuView.setInfoMessage(MainMenuView.MessageType.EMPTY, "");
        comicBookView.resetImagePan();
    }


    /**
     * This method will get all the books from the path that the user
     * has inputted in the text area.
     *
     * @return List of Path
     * @throws Exception Can throw filesystem Exceptions
     */
    private List<Path> getBooksFromPath() throws Exception {
        Path booksPath = Paths.get(mainMenuView.getPathTextAreaText());


        try (Stream<Path> pathStream = Files.walk(booksPath)) {
            return pathStream.filter(Files::isRegularFile)
                    .filter(file -> checkFileExtension(file.toString()))
                    .toList();
        }
    }


    /**
     * This method will check if the file extension is a supported comic book
     * file extension.
     *
     * @param filePath string containing the path to the file
     * @return boolean
     */
    private boolean checkFileExtension(final String filePath) {
        // Supported file extensions
        return filePath.endsWith(".cbr")
                || filePath.endsWith(".cbz")
                || filePath.endsWith(".nhlcomic");
    }
}
