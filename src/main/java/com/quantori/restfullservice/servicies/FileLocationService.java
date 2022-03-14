package com.quantori.restfullservice.servicies;

import com.quantori.restfullservice.exeption.ResourceNotFoundException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.multipart.MultipartFile;

public interface FileLocationService {
    Long save(MultipartFile image,Long userId) throws Exception;
    FileSystemResource find(Long imageId, Long userId) throws ResourceNotFoundException;
}
