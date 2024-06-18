package org.caesar.userservice.Data.Services.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.userservice.Data.Dao.ProfilePicRepository;
import org.caesar.userservice.Data.Entities.ProfilePic;
import org.caesar.userservice.Data.Services.ProfilePicService;
import org.caesar.userservice.Dto.ProfilePicDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfilePicServiceImpl implements ProfilePicService {

    private final ProfilePicRepository profilePicRepository;


    @Override
    public boolean saveImage(String username, MultipartFile file) {
        try {
            //Ricerca sul db della vecchia foto profilo per eseguire l'aggiornamento
            ProfilePic profilePic = profilePicRepository.findByUserUsername(username);

            profilePic.setProfilePic(file.getBytes());
            profilePicRepository.save(profilePic);

            return true;
        }catch (Exception | Error e){
            log.debug("Errore nel caricamento dell'imaggine");
            return false;
        }
    }

    @Override
    public byte[] getUserImage(String username) {
        return profilePicRepository.findByUserUsername(username).getProfilePic();
    }
}