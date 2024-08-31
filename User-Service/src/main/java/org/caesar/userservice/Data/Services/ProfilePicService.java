package org.caesar.userservice.Data.Services;

import org.caesar.userservice.Dto.ProfilePicDTO;
import org.springframework.web.multipart.MultipartFile;

public interface ProfilePicService {

    boolean saveImage(String username, MultipartFile file, boolean save);
    byte[] getUserImage(String username);

    //TODO 2PC PER ELIMINAZIONE UTENTE
    ProfilePicDTO validateDeleteUser(String username, boolean rollback);
    boolean releaseDeleteUser(String username);
}
