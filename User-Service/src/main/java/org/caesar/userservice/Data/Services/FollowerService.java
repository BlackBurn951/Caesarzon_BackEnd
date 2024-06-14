package org.caesar.userservice.Data.Services;

import org.caesar.userservice.Dto.FollowerDTO;
import org.caesar.userservice.Dto.UserSearchDTO;

import java.util.List;

public interface FollowerService {
    boolean addFollowers(String username1, List<UserSearchDTO> followers);
    FollowerDTO getFollower(String username1, String username2);
    List<FollowerDTO> getFollowersOrFriends(String username1, int fwl, boolean friend);
    boolean deleteFollowers(String username1, List<String> followers);
}
