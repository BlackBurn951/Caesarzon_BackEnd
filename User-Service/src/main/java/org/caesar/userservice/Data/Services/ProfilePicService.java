package org.caesar.userservice.Data.Services;

import org.springframework.web.multipart.MultipartFile;

public interface ProfilePicService {

    boolean saveImage(MultipartFile file);
//    MultipartFile getImage();
    byte[] getUserImage(String username);

}
