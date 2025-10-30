package xyz.jelmer8.models;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.AbstractFileHeader;
import net.lingala.zip4j.model.FileHeader;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;

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

        List<FileHeader> fileHeaderList = zipFile.getFileHeaders();

        fileHeaderList.sort(Comparator.comparing(AbstractFileHeader::getFileName));

        for (FileHeader fileHeader : fileHeaderList) {
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
        }

        zipFile.close();
    }
}
