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
    private final ModelMapper modelMapper;

    //Salva l'immagine dell'utente
    @Override
    public boolean saveImage(String username, MultipartFile file, boolean save) {
        try {
            // Converti l'username in minuscolo
            String lowercaseUsername = username.toLowerCase();

            // Ricerca sul db della vecchia foto profilo per eseguire l'aggiornamento in caso ne aggiunge una nuova
            if (save) {
                ProfilePic profilePic1 = new ProfilePic();
                profilePic1.setUserUsername(lowercaseUsername);
                profilePic1.setProfilePic(file.getBytes());
                profilePicRepository.save(profilePic1);
            } else {
                ProfilePic profilePic = profilePicRepository.findByUserUsername(lowercaseUsername);
                profilePic.setProfilePic(file.getBytes());
                profilePicRepository.save(profilePic);
            }
            return true;
        } catch (Exception | Error e) {
            log.debug("Errore nel caricamento dell'immagine");
            return false;
        }
    }


    //Metodo per restituire l'immagine dell'utente
    @Override
    public byte[] getUserImage(String username) {
        return profilePicRepository.findByUserUsername(username).getProfilePic();
    }



    @Override
    public ProfilePicDTO validateDeleteUser(String username, boolean rollback) {
        try {
            ProfilePic image= profilePicRepository.findByUserUsername(username);

            if(image==null)
                return null;

            image.setOnDeleting(!rollback);

            profilePicRepository.save(image);

            return modelMapper.map(image, ProfilePicDTO.class);
        }catch (Exception | Error e){
            log.debug("Errore nel caricamento dell'imaggine");
            return null;
        }
    }

    @Override
    public boolean releaseDeleteUser(String username) {
        try {
            profilePicRepository.deleteByUserUsername(username);

            return true;
        }catch (Exception | Error e){
            log.debug("Errore nel caricamento dell'imaggine");
            return false;
        }
    }
}