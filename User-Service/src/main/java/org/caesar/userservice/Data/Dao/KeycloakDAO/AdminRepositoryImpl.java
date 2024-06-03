package org.caesar.userservice.Data.Dao.KeycloakDAO;

import lombok.RequiredArgsConstructor;
import org.caesar.userservice.Data.Entities.Admin;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AdminRepositoryImpl implements AdminRepository {

    //Oggetto per la comunicazione con keycloak
    private final Keycloak keycloak;




    @Override
    public Admin findAdminById(String id) {
        RealmResource realmResource = keycloak.realm("CaesarRealm");
        UserResource userResource = realmResource.users().get(id);

        UserRepresentation userRepresentation = userResource.toRepresentation();

        Admin admin = new Admin();

        admin.setId(userRepresentation.getId());
        admin.setFirstName(userRepresentation.getFirstName());
        admin.setLastName(userRepresentation.getLastName());
        admin.setUsername(userRepresentation.getUsername());
        admin.setEmail(userRepresentation.getEmail());

        return admin;
    }

    @Override
    public Admin findAdminByEmail(String email) {
        return setAdmin(true, email);
    }

    @Override
    public Admin findAdminByUsername(String username) {
        return setAdmin(false, username);
    }

    public Admin setAdmin(boolean type, String field) {
        RealmResource realmResource = keycloak.realm("CaesarRealm");
        List<UserRepresentation> usersResource;

        if(type){
            usersResource = realmResource.users().searchByEmail(field, true);
        }else{
            usersResource = realmResource.users().searchByUsername(field, true);
        }

        Admin admin = new Admin();

        admin.setId(usersResource.getFirst().getId());
        admin.setFirstName(usersResource.getFirst().getFirstName());
        admin.setLastName(usersResource.getFirst().getLastName());
        admin.setUsername(usersResource.getFirst().getUsername());
        admin.setEmail(usersResource.getFirst().getEmail());

        return admin;
    }
}
