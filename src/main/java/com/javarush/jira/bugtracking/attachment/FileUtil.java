package com.javarush.jira.bugtracking.attachment;

import com.javarush.jira.common.error.IllegalRequestDataException;
import com.javarush.jira.common.error.NotFoundException;
import lombok.experimental.UtilityClass;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@UtilityClass
public class FileUtil {
    private static final String ATTACHMENT_PATH = "./attachments/%s/";

    public static void upload(MultipartFile multipartFile, String directoryPath, String fileName) {
        if (multipartFile.isEmpty()) {
            throw new IllegalRequestDataException("Select a file to upload.");
        }

        File dir = new File(directoryPath);
        if (dir.exists() || dir.mkdirs()) {
            File file = new File(getAttachmentPath(directoryPath, fileName).toString());
	        try {
				multipartFile.transferTo(file);
			} catch (IOException e) {
					throw new IllegalRequestDataException("Failed to upload file" + multipartFile.getOriginalFilename());
			}
        }
    }

    public static Resource download(String fileLink) {
        Path path = Paths.get(fileLink);
        try {
            Resource resource = new UrlResource(path.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new IllegalRequestDataException("Failed to download file " + resource.getFilename());
            }
        } catch (MalformedURLException ex) {
            throw new NotFoundException("File" + fileLink + " not found");
        }
    }

    public static void delete(String fileLink) {
        Path path = Paths.get(fileLink);
        try {
            Files.delete(path);
        } catch (IOException ex) {
            throw new IllegalRequestDataException("File" + fileLink + " deletion failed.");
        }
    }

    public static String getPath(String titleType) {
        return String.format(ATTACHMENT_PATH, titleType.toLowerCase());
    }
    
    public static String getNormalizedName(String fileName) {
    	return Paths.get( fileName.replaceAll("[\\\\/:*?\"<>|]", "_")).normalize().toString();
    }
    
    public static Path getAttachmentPath(String directoryPath, String fileName) {
    	Path uploadDir = Paths.get(directoryPath).toAbsolutePath().normalize();
    	Path attachmentPath = uploadDir.resolve(getNormalizedName(fileName));
    	
    	if(!attachmentPath.startsWith(uploadDir)) {
    		throw new SecurityException("Path traversal detected");
    	}
    	return attachmentPath;
    }
    
    
}
