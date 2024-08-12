package org.caesar.userservice.Data.Services.Impl;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.userservice.Data.Dao.ProfilePicRepository;
import org.caesar.userservice.Data.Entities.ProfilePic;
import org.caesar.userservice.Data.Services.ProfilePicService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
@RequiredArgsConstructor
@Slf4j
public class ProfilePicServiceImpl implements ProfilePicService {

    private final ProfilePicRepository profilePicRepository;

    //Salva l'immagine dell'utente
    @Override
    public boolean saveImage(String username, MultipartFile file, boolean save) {
        try {
            //Ricerca sul db della vecchia foto profilo per eseguire l'aggiornamento in caso ne aggiunge una nuova
            if(save){
                ProfilePic profilePic1 = new ProfilePic();
                profilePic1.setUserUsername(username);
                profilePic1.setProfilePic(file.getBytes());
                profilePicRepository.save(profilePic1);
            }else{
                ProfilePic profilePic = profilePicRepository.findByUserUsername(username);
                profilePic.setProfilePic(file.getBytes());
                profilePicRepository.save(profilePic);
            }
            return true;
        }catch (Exception | Error e){
            log.debug("Errore nel caricamento dell'imaggine");
            return false;
        }
    }

    //Metodo per restituire l'immagine dell'utente
    @Override
    public byte[] getUserImage(String username) {
        return profilePicRepository.findByUserUsername(username).getProfilePic();
    }
}