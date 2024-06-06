package org.caesar.userservice.Data.Services;

import org.caesar.userservice.Dto.UserAddressDTO;

import java.util.List;

public interface UserAddressService {

    boolean addUserAddreses(UserAddressDTO userAddress);
    UserAddressDTO getUserAddress(String userId, int addressNum);
    List<String> getAddresses();
}
