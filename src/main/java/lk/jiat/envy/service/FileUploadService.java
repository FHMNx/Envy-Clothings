package lk.jiat.envy.service;

import jakarta.servlet.ServletContext;
import jakarta.ws.rs.WebApplicationException;
import lk.jiat.envy.util.Env;
import org.apache.commons.io.FilenameUtils;
import org.glassfish.jersey.media.multipart.ContentDisposition;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUploadService {

    private static final String UPLOAD_DIRECTORY_NAME = "uploads";
    private final ServletContext context;

    public FileItem uploadFile(String directoryName, InputStream inputStream, ContentDisposition fileMetaData) {
        return writeFile(UPLOAD_DIRECTORY_NAME + "/" + directoryName, inputStream, fileMetaData);
    }

    public FileUploadService(ServletContext context) {
        this.context = context;
    }

    private FileItem writeFile(String pathName, InputStream inputStream, ContentDisposition contentDisposition) {
        Path uploadPath = Paths.get(context.getRealPath(pathName));
        String extension = FilenameUtils.getExtension(contentDisposition.getFileName());
        String fileName = System.currentTimeMillis() + "." + extension;

        if (!Files.exists(uploadPath)) {
            try {
                System.out.println("Upload Path not Found. Creating Directory : \"" + uploadPath + "\"");
                Files.createDirectories(uploadPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            int read;
            byte[] bytes = new byte[1024];
            OutputStream outputStream = new FileOutputStream(uploadPath + "/" + fileName);
            while ((read = inputStream.read(bytes)) != -1) {                                              //READING FINISHED[-1]
                outputStream.write(bytes, 0, read);
            }
            outputStream.flush();
            outputStream.close();

        } catch (IOException e) {
            throw new WebApplicationException("Error while file uploading! Try again...");
        }

        String relativePath = pathName + "/" + fileName;
        String fullUrl = context.getContextPath() + relativePath;

        return new FileItem( fileName, contentDisposition.getFileName(), relativePath, fullUrl,
                uploadPath.toString() + "/" + fileName
        );
    }

    public static class FileItem {
        private String fileName;
        private String originalFileName;
        private String relativePath;
        private String fullUrl;
        private String diskPath;

        public FileItem(String fileName, String originalFileName,
                        String relativePath, String fullUrl, String diskPath) {
            this.fileName = fileName;
            this.originalFileName = originalFileName;
            this.relativePath = relativePath;
            this.fullUrl = fullUrl;
            this.diskPath = diskPath;
        }

        public String getRelativePath() {
            return relativePath;
        }

        public void setRelativePath(String relativePath) {
            this.relativePath = relativePath;
        }

        public String getDiskPath() {
            return diskPath;
        }

        public void setDiskPath(String diskPath) {
            this.diskPath = diskPath;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getOriginalFileName() {
            return originalFileName;
        }

        public void setOriginalFileName(String originalFileName) {
            this.originalFileName = originalFileName;
        }


        public String getFullUrl() {
            return fullUrl;
        }

        public void setFullUrl(String fullUrl) {
            this.fullUrl = fullUrl;
        }

    }

}
