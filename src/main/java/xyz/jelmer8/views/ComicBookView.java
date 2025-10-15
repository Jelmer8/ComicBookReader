package xyz.jelmer8.views;

import xyz.jelmer8.models.ImageModel;

import javax.swing.JPanel;
import java.awt.Panel;
import java.awt.FlowLayout;
import java.awt.Dimension;
import java.awt.Button;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import java.awt.event.ActionListener;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;

import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import java.io.Serial;

public class ComicBookView extends Panel {

    /** The comic book image with original size. */
    private BufferedImage originalComicBookImage;

    /** The comic book image. */
    private BufferedImage comicBookImage;

    /** Offset of the image relative to the frame due to panning. */
    private final int[] imageOffset = new int[] {0, 0};

    /** Used for calculating imageOffset. */
    private final int[] cursorOffset = new int[] {0, 0};

    /** The JPanel where the comic book image is drawn on. */
    private final JPanel imageJPanel;

    /** The return button. */
    private final Button returnButton;

    /** The previous page button. */
    private final Button previousPageButton;

    /** The next page button. */
    private final Button nextPageButton;


    /**
     * Constructor for the ComicBookView.
     * @param viewSize The size of the view panel
     */
    public ComicBookView(final Dimension viewSize) {
        setSize(viewSize);
        setLayout(new FlowLayout());
        setIgnoreRepaint(true);

        returnButton = new Button("Stop Reading");
        add(returnButton);

        previousPageButton = new Button("<");
        add(previousPageButton);

        nextPageButton = new Button(">");
        add(nextPageButton);

        imageJPanel = createImagePanel();

        add(imageJPanel);
        resetImagePan();
    }


    /**
     * Method to set the button listeners for the buttons.
     * @param returnListener The listener for the return button
     * @param previousPageListener The listener for the previous page button
     * @param nextPageListener The listener for the next page button
     */
    public void setButtonListeners(final ActionListener returnListener,
                                   final ActionListener previousPageListener,
                                   final ActionListener nextPageListener) {
        returnButton.addActionListener(returnListener);
        previousPageButton.addActionListener(previousPageListener);
        nextPageButton.addActionListener(nextPageListener);
    }


    /**
     * Method to set the MouseMotion and Mouse Listeners to the JPanel.
     * @param motionListener the MouseMotionListener to set
     * @param mouseListener the MouseListener to set
     */
    public void setPanelListener(final MouseMotionListener motionListener,
                                  final MouseListener mouseListener) {
        imageJPanel.addMouseMotionListener(motionListener);
        imageJPanel.addMouseListener(mouseListener);
    }

    public void addComponentListener(final ComponentListener componentListener) {
        super.addComponentListener(componentListener);
    }


    /**
     * Method to set the comic book image.
     * @param imageModel The ImageModel to set
     */
    public void setComicBookImage(final ImageModel imageModel) {
        // Set the image to the panel

        final BufferedImage image = imageModel.image();

        originalComicBookImage = image;
        comicBookImage = image;

        // If the frame is wider than the original image,
        // the image will get sized to fit the frame
        if (this.getWidth() > image.getWidth()) {
            comicBookImage = createScaledImage();
        }


        // Repaint panel and reset pan
        imageJPanel.repaint();
        resetImagePan();
    }


    /**
     * Method to resize the comic book image to current frame size.
     */
    public void resizeComicBookImage() {
        // Set the image to the panel

        if (comicBookImage == null) {
            return; // No image to resize
        }

        // This variable is used a lot in this method
        int originalImageWidth = originalComicBookImage.getWidth();

        // If the image is already the same size as the frame or original image,
        // no resizing is needed
        if (this.getWidth() == comicBookImage.getWidth()
                && !(this.getWidth() <= originalImageWidth
                && comicBookImage.getWidth() != originalImageWidth)) {
            return;
        }

        // If the frame is wider than the original image,
        // the image will get sized to fit the frame
        if (this.getWidth() > originalImageWidth) {
            comicBookImage = createScaledImage();
        } else if (this.getWidth() <= originalImageWidth) {
            // If the frame is smaller than the original image,
            // the original image will be used
            comicBookImage = originalComicBookImage;
        }

        resetImagePan();
    }


    /**
     * Method to create a correct scaled image of the current comic book image.
     * @return BufferedImage the scaled image
     */
    private BufferedImage createScaledImage() {
        int width = originalComicBookImage.getWidth();
        double factor = (double) this.getWidth() / width;

        // Calculate the new height of the image,
        // height is scaled by the same factor as the width
        int newHeight = (int) (originalComicBookImage.getHeight() * factor);


        BufferedImage after = new BufferedImage(this.getWidth(), newHeight,
                originalComicBookImage.getType());

        Graphics2D g = after.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(originalComicBookImage, 0, 0, this.getWidth(), newHeight,
                0, 0, originalComicBookImage.getWidth(),
                originalComicBookImage.getHeight(), null);
        g.dispose();
        return after;
    }


    /**
     * Method to create the panel for showing the image.
     * @return JPanel The created image panel
     */
    private JPanel createImagePanel() {
        return new JPanel() {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            public Dimension getPreferredSize() {
                if (comicBookImage == null) {
                    return new Dimension(0, 0);
                }

                // Return the size of the image
                return new Dimension(comicBookImage.getWidth(),
                        comicBookImage.getHeight());
            }

            @Override
            public void paintComponent(final Graphics g) {
                super.paintComponent(g);
                g.drawImage(comicBookImage, 0, 0, this);
            }
        };
    }


    /**
     * Method to pan the comic book image.
     * @param x The x coordinate to pan to
     * @param y The y coordinate to pan to
     */
    public void panComicBookImage(final int x, final int y) {
        imageOffset[0] += (x - cursorOffset[0]);
        imageOffset[1] += (y - cursorOffset[1]);

        cursorOffset[0] = x;
        cursorOffset[1] = y;

        // Restrict pan-movement
        restrictPanning();

        imageJPanel.setLocation(imageOffset[0], imageOffset[1]);
    }


    /**
     * Method to reset the comic book pan.
     */
    public void resetImagePan() {
        imageOffset[0] = 0;
        imageOffset[1] = 0;
        imageJPanel.setLocation(imageOffset[0], imageOffset[1]);
    }


    /**
     * Method to restrict the panning of the comic book image.
     */
    private void restrictPanning() {
        // If the frame is wider than the original image, the image will get
        // sized up to fit the frame thus no panning on x-axis is needed
        if (this.getWidth() > originalComicBookImage.getHeight()) {
            imageOffset[0] = 0;
        } else {
            int errorMarginX = 25;

            // If the image is out of view on the left,
            // move it back in view
            if (imageOffset[0] < -originalComicBookImage.getWidth()
                    + this.getWidth() - errorMarginX) {
                imageOffset[0] = -originalComicBookImage.getWidth()
                        + this.getWidth() - errorMarginX;
            }

            // Panning the image to the right is unneeded
            if (imageOffset[0] > 0) {
                imageOffset[0] = 0;
            }
        }

        int errorMarginY = 25;

        // If the image is out of view on the top, move it back in view
        if (imageOffset[1] < -comicBookImage.getHeight()
                + this.getHeight() - errorMarginY) {
            imageOffset[1] = -comicBookImage.getHeight()
                    + this.getHeight() - errorMarginY;
        }

        // Panning the image upwards
        if (imageOffset[1] > 0) {
            imageOffset[1] = 0;
        }
    }


    /**
     * Method to set the cursor offset relative to the image.
     * Used to calculate the pan movement.
     * @param x The x coordinate of the cursor
     * @param y The y coordinate of the cursor
     */
    public void setMousePressed(final int x, final int y) {
        cursorOffset[0] = x;
        cursorOffset[1] = y;
    }


    /**
     * Method to clear the comic book image from the JPanel.
     */
    public void clearImage() {
        comicBookImage = null;
        imageJPanel.repaint();
    }
}
