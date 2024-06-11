package org.caesar.userservice.Data.Services;

import org.caesar.userservice.Dto.AddressDTO;
import org.caesar.userservice.Dto.UserAddressDTO;

import java.util.List;
import java.util.UUID;


public interface AddressService {

    boolean saveAddress(AddressDTO addressDTO);
    AddressDTO getAddress(String addressName);
    boolean deleteAddress(UUID addressId);
    boolean deleteAllUserAddresses(String userId, List<UserAddressDTO> userAddresses);
}
