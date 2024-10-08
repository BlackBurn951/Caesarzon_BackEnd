package org.caesar.userservice.Data.Services;

import org.caesar.userservice.Dto.AddressDTO;
import org.caesar.userservice.Dto.CardDTO;
import org.caesar.userservice.Dto.UserAddressDTO;

import java.util.List;
import java.util.UUID;


public interface AddressService {

    UUID addAddress(AddressDTO addressDTO);
    AddressDTO getAddress(UUID addressId);
    boolean deleteAddress(UUID addressId);

    boolean validateOrRollbackAddresses(List<UUID> addressId, boolean rollback);
    boolean releaseLockAddresses(List<UUID> addressId);
}
