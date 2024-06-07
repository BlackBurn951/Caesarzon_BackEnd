package org.caesar.userservice.Data.Services;

import org.caesar.userservice.Dto.AddressDTO;


public interface AddressService {

    boolean saveAddress(AddressDTO addressDTO);
    AddressDTO getAddress(String addressName);
    boolean deleteAddress(String addressName);
    boolean deleteAllUserAddresses(String userId);
}
