package org.caesar.userservice.Data.Services.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.userservice.Data.Dao.FollowerRepository;
import org.caesar.userservice.Data.Entities.Address;
import org.caesar.userservice.Data.Entities.Follower;
import org.caesar.userservice.Data.Services.FollowerService;
import org.caesar.userservice.Dto.AddressDTO;
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
            if(f.isOnDeleting())
                continue;
            followerDTOs.add(modelMapper.map(f, FollowerDTO.class));
        }

        return followerDTOs;
    }

    @Override
    public FollowerDTO getFollower(String username1, String username2) {
        Follower follower= followerRepository.findByUserUsername1AndUserUsername2(username1, username2);

        if(follower == null || follower.isOnDeleting())
            return null;
        return modelMapper.map(follower, FollowerDTO.class);
    }

    //Aggiunta del follower
    @Override
    public boolean addFollowers(String username1, List<UserSearchDTO> followers) {
        System.out.println("Follower cher arriva: "+followers.getFirst().getUsername()+" "+followers.getFirst().isFriend());
        //Controllo che venga inviato almeno un dato
        if(followers==null || followers.isEmpty()) {
            return false;
        }
        List<UserSearchDTO> followz = new Vector<>();

        for(UserSearchDTO f: followers) {
            Follower follower  = followerRepository.findByUserUsername1AndUserUsername2(username1, f.getUsername());
            if(follower!=null && !follower.isOnDeleting()){
                System.out.println("Follower trovato: "+f.getUsername()+" stato: "+f.isFriend());
                follower.setFriend(f.isFriend());
                System.out.println("Follower trovato: "+follower.getUserUsername2()+" nuovo stato: "+follower.isFriend());
                followerRepository.save(follower);
            }else if(follower==null){
                followz.add(f);
            }
        }

        List<Follower> savingFollower= new Vector<>();

        Follower fwl= new Follower();

        for(UserSearchDTO follower : followz) {
            fwl.setUserUsername1(username1);
            fwl.setUserUsername2(follower.getUsername());
            fwl.setFriend(follower.isFriend());
            fwl.setOnDeleting(false);

            savingFollower.add(fwl);
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
        Follower fwl= followerRepository.findByUserUsername1AndUserUsername2AndFriendIsTrue(username, friendUsername);

        return fwl!=null && !fwl.isOnDeleting();
    }

    //Eliminazione del singolo follower
    @Override
    public boolean deleteFollowers(String username1, String usernameToDelete) {
        try{
            Follower flw;
            //Ricerca e presa di tutti gli oggetti entity per eseguire la cancellazione
            flw= followerRepository.findByUserUsername1AndUserUsername2(username1, usernameToDelete);

            if(flw!=null && !flw.isOnDeleting()) {
                followerRepository.delete(flw);

                return true;
            }
            return false;
        } catch (Exception | Error e) {
            log.debug("Errore nell'eliminazione dei follower");
            return false;
        }
    }



    @Override
    public List<FollowerDTO> validateOrRollbackDeleteFollowers(String username, boolean rollback) { //0 -> true 1-> false 2 -> non avente
        try {
            System.out.println("Appena entrato nel metodo");
            List<Follower> followers= followerRepository.findAllByUserUsername1OrUserUsername2(username);

            System.out.println("Subito dopo la presa");
            if(followers.isEmpty())
                return new Vector<>();

            if(followers.getFirst().isOnDeleting() && !rollback)
                return null;

            List<FollowerDTO> result= new Vector<>();
            for(Follower follower : followers){
                System.out.println("User 1 "+ follower.getUserUsername1()+" "+follower.getUserUsername2());
                result.add(modelMapper.map(follower, FollowerDTO.class));

                follower.setOnDeleting(!rollback);
            }

            followerRepository.saveAll(followers);
            return result;
        } catch (Exception | Error e) {
            System.out.println(e);
            log.debug("Errore nella validazione o nel rollback pre completamento dei follower ");
            return null;
        }
    }

    @Override
    public boolean releaseOrDeleteFollowers(String username) {
        try {
            followerRepository.deleteAllByUserUsername1(username);
            followerRepository.deleteAllByUserUsername2(username);

            return true;
        } catch (Exception | Error e) {
            log.debug("Errore nel rilascio del lock dei follower");
            return false;
        }
    }

}