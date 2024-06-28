package org.caesar.userservice.Data.Services;

import org.caesar.userservice.Dto.FollowerDTO;
import org.caesar.userservice.Dto.UserSearchDTO;

import java.util.List;

public interface FollowerService {
    boolean addFollowers(String username1, List<UserSearchDTO> followers);
    List<FollowerDTO> getFollowersOrFriends(String username1, int fwl, boolean friend);
    boolean isFriend(String username, String friendUsername);
    boolean deleteFollowers(String username1, List<String> followers);
     boolean deleteFollowersByUsername(String username);
}
