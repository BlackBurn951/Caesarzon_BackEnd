package org.caesar.userservice.Data.Services;

import org.caesar.userservice.Dto.AddressDTO;
import org.caesar.userservice.Dto.UserAddressDTO;

import java.util.List;
import java.util.UUID;

public interface UserAddressService {

    boolean addUserAddreses(UserAddressDTO userAddress);
    UserAddressDTO getUserAddress(UUID id);
    List<UUID> getAddresses(String userUsername);
    List<UserAddressDTO> getUserAddresses(String userUsername);
    boolean checkAddress(String username, AddressDTO addressId);
    boolean deleteUserAddress(UserAddressDTO userAddressDTO);
    boolean deleteUserAddresses(String userUsername);
}
