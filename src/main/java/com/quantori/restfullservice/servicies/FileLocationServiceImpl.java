package com.quantori.restfullservice.servicies;

import com.quantori.restfullservice.exeption.BadRequestException;
import com.quantori.restfullservice.exeption.FileSystemRepositoryException;
import com.quantori.restfullservice.exeption.ResourceNotFoundException;
import com.quantori.restfullservice.models.Image;
import com.quantori.restfullservice.models.User;
import com.quantori.restfullservice.properties.DataProperties;
import com.quantori.restfullservice.repositories.FileSystemRepository;
import com.quantori.restfullservice.repositories.ImageDbRepository;
import com.quantori.restfullservice.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class FileLocationServiceImpl implements FileLocationService {

    final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private LocalDate localDate = LocalDate.now();

    private UserRepository userRepository;
    private ImageDbRepository imageDbRepository;
    private FileSystemRepository fileSystemRepository;
    private DataProperties dataProperties;

    @Autowired
    public FileLocationServiceImpl( UserRepository userRepository, ImageDbRepository imageDbRepository, FileSystemRepository fileSystemRepository, DataProperties dataProperties) {
        this.userRepository = userRepository;
        this.imageDbRepository = imageDbRepository;
        this.fileSystemRepository = fileSystemRepository;
        this.dataProperties = dataProperties;
    }

    @Override
    public Long save(MultipartFile image, Long userId) throws BadRequestException, IOException, FileSystemRepositoryException, ResourceNotFoundException {

        if (image == null || image.isEmpty()){
            throw new BadRequestException("There is no data in request");
        }

        Optional<User> optionalUser = userRepository.findById(userId);
        if (!optionalUser.isPresent()){
            throw new ResourceNotFoundException("User is not founded");
        }

        Image dbImage = new Image();
        dbImage.setName(image.getOriginalFilename());
        if(dataProperties.getSAVELOCAL()) {
            String location = fileSystemRepository.save(image.getBytes(), image.getOriginalFilename());
            dbImage.setLocation(location);
            dbImage.setUser(userRepository.findById(userId).get());
            Image id = imageDbRepository.save(dbImage);
            return imageDbRepository.save(dbImage)
                    .getId();
        }

        throw new FileSystemRepositoryException("Error: Image isn't saved!!!");
    }

    @Override
    public FileSystemResource find(Long imageId, Long userId) throws ResourceNotFoundException {

        Optional<Image> optional = Optional.ofNullable(imageDbRepository.findByImageIdAndUserId(imageId, userId));
        if (!optional.isPresent()){
            throw new ResourceNotFoundException(String.format("Image with imageId: %d and userId: %d is not exists.",
                    imageId, userId));
        }

        return fileSystemRepository.findInFileSystem(optional.get().getLocation());
    }
}
