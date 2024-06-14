package org.caesar.userservice.Data.Services.Impl;

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

    @Override
    public boolean addFollowers(String username1, List<UserSearchDTO> followers) {
        if(followers==null || followers.isEmpty())
            return false;

        List<Follower> savingFollower= new Vector<>();

        FollowerDTO fwl= new FollowerDTO();

        for(UserSearchDTO follower : followers) {
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
    public FollowerDTO getFollower(String username1, String username2) {
        return modelMapper.map(followerRepository.findByUserUsername1AndUserUsername2(username1, username2), FollowerDTO.class);
    }

    @Override
    public List<FollowerDTO> getFollowersOrFriends(String username1, int flw, boolean friend) {
        List<Follower> followers;

        if(friend)
            followers= followerRepository.findAllByUserUsername1(username1, PageRequest.of(flw, 20));
        else
            followers= followerRepository.findAllByUserUsername1AndFriend(username1, true, PageRequest.of(flw, 20));

        List<FollowerDTO> followerDTOs= new Vector<>();

        for(Follower f: followers) {
            followerDTOs.add(modelMapper.map(f, FollowerDTO.class));
        }

        return followerDTOs;
    }

    @Override
    public boolean deleteFollowers(String username1, List<String> followers) {
        List<Follower> savedFollowers= new Vector<>();

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
}