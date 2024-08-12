package org.caesar.userservice.Data.Services.Impl;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.userservice.Data.Dao.FollowerRepository;
import org.caesar.userservice.Data.Entities.Follower;
import org.caesar.userservice.Data.Services.FollowerService;
import org.caesar.userservice.Dto.FollowerDTO;
import org.caesar.userservice.Dto.UserSearchDTO;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Vector;

@Service
@RequiredArgsConstructor
@Slf4j
public class FollowerServiceImpl implements FollowerService {

    private final FollowerRepository followerRepository;
    private final ModelMapper modelMapper;

    //Rstituisce la lista di seguiti e amici dell'utente
    @Override
    public List<FollowerDTO> getFollowersOrFriends(String username1, int flw, boolean friend) {
        List<Follower> followers;

        //Controllo se si vuole prendere i follower o gli amici
        if(!friend)
            followers= followerRepository.findAllByUserUsername1(username1, PageRequest.of(flw, 20));
        else
            followers= followerRepository.findAllByUserUsername1AndFriend(username1, true, PageRequest.of(flw, 20));

        //Conversione degli eventuali oggetti entity in DTO
        List<FollowerDTO> followerDTOs= new Vector<>();

        for(Follower f: followers) {
            followerDTOs.add(modelMapper.map(f, FollowerDTO.class));
        }

        return followerDTOs;
    }

    @Override
    public FollowerDTO getFollower(String username1, String username2) {
        Follower follower= followerRepository.findByUserUsername1AndUserUsername2(username1, username2);

        if(follower == null)
            return null;
        return modelMapper.map(follower, FollowerDTO.class);
    }
    //Aggiunta del follower
    @Override
    public boolean addFollowers(String username1, List<UserSearchDTO> followers) {
        //Controllo che venga inviato almeno un dato
        if(followers==null || followers.isEmpty())
            return false;
        List<UserSearchDTO> followz = new Vector<>();

        for(UserSearchDTO f: followers) {
            Follower follower  = followerRepository.findByUserUsername1AndUserUsername2(username1, f.getUsername());
            if(follower!=null){
                follower.setFriend(f.isFriend());
                followerRepository.save(follower);
            }else{
                followz.add(f);
            }

        }

        List<Follower> savingFollower= new Vector<>();

        FollowerDTO fwl= new FollowerDTO();

        for(UserSearchDTO follower : followz) {
            fwl.setUserUsername1(username1);
            fwl.setUserUsername2(follower.getUsername());
            fwl.setFriend(follower.isFriend());

            savingFollower.add(modelMapper.map(fwl, Follower.class));
        }

        try {
            followerRepository.saveAll(savingFollower);
            return true;
        } catch (Exception | Error e) {
            log.debug("Errore nel salvataggio dei follower");
            return false;
        }
    }

    @Override
    public boolean isFriend(String username, String friendUsername) {
        return followerRepository.findByUserUsername1AndUserUsername2(username, friendUsername)!=null;
    }
    //Eliminazione del singolo follower
    @Override
    public boolean deleteFollowers(String username1, List<String> followers) {
        List<Follower> savedFollowers= new Vector<>();

        //Ricerca e presa di tutti gli oggetti entity per eseguire la cancellazione
        for(String follower : followers) {
            savedFollowers.add(followerRepository.findByUserUsername1AndUserUsername2(username1, follower));
        }

        try{
            followerRepository.deleteAll(savedFollowers);

            return true;
        } catch (Exception | Error e) {
            log.debug("Errore nell'eliminazione dei follower");
            return false;
        }
    }

    //TODO VA CHIAMATA NEL GENERAL ALL'ELIMINAZIONE DELL'UTENTE

    //Metodo per la cancellazione di tutti i follower da tutte e due la parti
    @Override
    public boolean deleteFollowersByUsername(String username){
        try{
            followerRepository.deleteAllByUserUsername1OrUserUsername2(username, username);
            return true;
        } catch (Exception | Error e) {
            log.debug("Errore nell'eliminazione dei follower");
            return false;
        }
    }
}