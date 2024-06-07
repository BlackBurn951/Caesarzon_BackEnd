package org.caesar.userservice.Data.Services;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProfilePicService {

    boolean saveImage(MultipartFile file);
    MultipartFile getImage();
}
