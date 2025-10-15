package xyz.jelmer8.models;

import net.lingala.zip4j.ZipFile;

import javax.imageio.ImageIO;
import java.io.IOException;

public class ZipBookModel extends BookModel {

    /** Constructor of the ZipBookModel class.
     * @param bookPath The path of the book
     */
    public ZipBookModel(String bookPath) {
        super(bookPath);
    }

    /**
     * Method to unzip the current set book.
     */
    public void decompressBook() throws IOException {
        ZipFile zipFile = new ZipFile(this.bookPath);
        zipFile.getFileHeaders().forEach(fileHeader -> {
            String fileName = fileHeader.getFileName();

            if (fileName.endsWith(".gif")) {
                try {
                    addGifToBookImages(zipFile.getInputStream(fileHeader));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }

            if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
                try {
                    bookImages.add(new ImageModel(ImageIO.read(
                            zipFile.getInputStream(fileHeader))));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        zipFile.close();
    }
}
