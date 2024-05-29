package org.caesar.userservice.Data.Dao.KeycloakDAO;

import lombok.RequiredArgsConstructor;
import org.caesar.userservice.Data.Entities.User;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;

@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    //Oggetti per la comunicazione con keycloak
    private final Keycloak keycloak;
    private final RealmResource realmResource = keycloak.realm("CaesarRealm");
    private final UsersResource usersResource = realmResource.users();
    private final UserRepresentation user = new UserRepresentation();

    @Override
    User findUserById(String id) {

    }

    @Override
    List<User> findAllUsers() {

    }

    @Override
    User findUserByEmail(String email) {

    }

    @Override
    User findUserByUsername(String username) {

    }
}
