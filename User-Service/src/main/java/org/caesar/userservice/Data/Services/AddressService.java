package org.caesar.userservice.Data.Services;

import org.caesar.userservice.Dto.AddressDTO;


public interface AddressService {

    boolean saveOrUpdateAddress(AddressDTO addressDTO, boolean isUpdate);
    AddressDTO getAddress(String addressName);
}
