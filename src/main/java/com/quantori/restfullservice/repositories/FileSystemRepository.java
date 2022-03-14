package com.quantori.restfullservice.repositories;

import com.quantori.restfullservice.exeption.FileSystemRepositoryException;
import com.quantori.restfullservice.properties.DataProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

@Repository
public class FileSystemRepository {

    @Autowired
    private DataProperties dataProperties;

    public String save(byte[] content, String imageName) throws FileSystemRepositoryException {

        Path newFile = Paths.get(dataProperties.getPATH() + new Date().getTime() + "-" + imageName);

        try {
            Files.createDirectories(newFile.getParent());
            Files.write(newFile, content);
        } catch (IOException e) {
            throw new FileSystemRepositoryException("Saving files to local drive error");
        }



        return newFile.toAbsolutePath()
                .toString();
    }

    public FileSystemResource findInFileSystem(String location) {
        try {
            return new FileSystemResource(Paths.get(location));
        } catch (Exception e) {
            // Handle access or file not found problems.
            throw new RuntimeException();
        }
    }
}
