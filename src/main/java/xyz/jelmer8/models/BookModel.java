package xyz.jelmer8.models;


import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public abstract class BookModel {

    /** The path of the current book. */
    String bookPath;

    /** The list of images of the current book. */
    final List<ImageModel> bookImages = new ArrayList<>();

    /** The current page of the book. */
    private int currentBookPage = 0;


    /**
     * Constructor of the BookModel class.
     * @param bookPath The path of the book
     */
    public BookModel(final String bookPath) {
        this.bookPath = bookPath;
    }


    /**
     * Method to decompress the book.
     */
    public abstract void decompressBook() throws Exception;


    /**
     * Method to add a gif to the list of book images.
     * @param is InputStream of the gif
     */
    void addGifToBookImages(final InputStream is) throws IOException {
        ImageReader reader = ImageIO.getImageReadersByFormatName("gif").next();
        ImageInputStream iis = ImageIO.createImageInputStream(is);
        reader.setInput(iis, false);

        int imageCount = reader.getNumImages(true);

        for (int i = 0; i < imageCount; i++) {
            bookImages.add(new ImageModel(reader.read(i)));
        }
    }


    /**
     * Getter for the list of images of the current book.
     *
     * @param index The index of the requested image
     * @return The requested image
     */
    public ImageModel getBookImage(final int index) {
        if (index != -1)
            currentBookPage = index;

        return bookImages.get(currentBookPage);
    }


    /**
     * Method to get the previous book image.
     * Used to go back a page in the comic book.
     * @return ImageModel of the previous book image,
     * or the first image if there is no previous image.
     */
    public ImageModel getPreviousBookImage() {
        if (currentBookPage == 0) {
            return bookImages.getFirst();
        }
        currentBookPage--;
        return bookImages.get(currentBookPage);
    }


    /**
     * Method to get the next book image.
     * Used to go forward a page in the comic book.
     * @return ImageModel of the next book image,
     * or the last image if there is no next image.
     */
    public ImageModel getNextBookImage() {
        if (currentBookPage == bookImages.size() - 1) {
            return bookImages.getLast();
        }
        currentBookPage++;
        return bookImages.get(currentBookPage);
    }
}
