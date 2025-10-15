package xyz.jelmer8.models;

import com.github.junrar.Archive;
import com.github.junrar.exception.RarException;
import com.github.junrar.rarfile.FileHeader;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class RarBookModel extends BookModel {

    /** Constructor of the RarBookModel class.
     * @param bookPath The path of the book
     */
    public RarBookModel(String bookPath) {
        super(bookPath);
    }

    /**
     * Method to unrar the current set book.
     */
    public void decompressBook() throws RarException, IOException {
        final Archive archive = new Archive(new File(this.bookPath));
        while (true) {
            FileHeader fileHeader = archive.nextFileHeader();

            // Break if there are no more files
            if (fileHeader == null) {
                break;
            }

            if (fileHeader.getFileName().endsWith(".gif")) {
                addGifToBookImages(archive.getInputStream(fileHeader));
                continue;
            }

            // Continue if file does not end with .jpg or .jpeg
            if ((!fileHeader.getFileName().endsWith(".jpg")
                    && !fileHeader.getFileName().endsWith(".jpeg"))) {
                continue;
            }

            // Add the image to the list of images
            try (InputStream is = archive.getInputStream(fileHeader)) {
                try {
                    bookImages.add(new ImageModel(ImageIO.read(is)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
