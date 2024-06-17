package org.caesar.userservice.Data.Services;

import org.springframework.web.multipart.MultipartFile;

public interface ProfilePicService {
    boolean saveImage(String username, MultipartFile file);
    byte[] getUserImage(String username);

}
