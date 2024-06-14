package org.caesar.userservice.Data.Services;

import org.caesar.userservice.Dto.UserAddressDTO;
import org.caesar.userservice.Dto.UserCardDTO;

import java.util.List;

public interface UserAddressService {

    boolean addUserAddreses(UserAddressDTO userAddress);
    UserAddressDTO getUserAddress(String userId, int addressNum);
    List<String> getAddresses(String userUsername);
    List<UserAddressDTO> getUserAddresses(String userUsername);
    boolean deleteUserAddress(UserAddressDTO userAddressDTO);
    boolean deleteUserAddresses(String userUsername);
}
