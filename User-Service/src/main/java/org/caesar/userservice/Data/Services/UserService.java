package org.caesar.userservice.Data.Services;

import org.caesar.userservice.Dto.*;

import java.util.List;


public interface UserService {

    UserDTO getUser(String username);
    List<UserDTO> getUsers(int start);
    List<String> getUsersByUsername(String username);
    boolean saveUser(UserRegistrationDTO userData);
    boolean updateUser(UserDTO userData);
    boolean deleteUser(String userId);
    boolean changePassword(PasswordChangeDTO passwordChangeDTO, String username);
    boolean checkOtp(PasswordChangeDTO passwordChangeDTO, String otp);
    boolean logout(String usermame, LogoutDTO logoutDTO);
}
