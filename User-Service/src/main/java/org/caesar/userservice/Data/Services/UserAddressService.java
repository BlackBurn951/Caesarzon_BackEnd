package org.caesar.userservice.Data.Services;

import org.caesar.userservice.Dto.AddressDTO;
import org.caesar.userservice.Dto.CardDTO;
import org.caesar.userservice.Dto.UserAddressDTO;

import java.util.List;
import java.util.UUID;

public interface UserAddressService {

    boolean addUserAddreses(UserAddressDTO userAddress);
    UserAddressDTO getUserAddress(UUID id);
    List<UUID> getAddresses(String userUsername);
    List<UserAddressDTO> getUserAddresses(String userUsername);
    boolean checkAddress(String username, UUID addressId);
    boolean deleteUserAddress(UserAddressDTO userAddressDTO);


    List<AddressDTO> validateOrRollbackUserAddressesDelete(String username, boolean rollback);
    boolean completeUserAddressesDelete(String username);
    boolean releaseLockUserAddresses(String username);
    boolean rollbackUserAddresses(String username, List<AddressDTO> addresses);
}
