package org.caesar.userservice.Data.Dao.KeycloakDAO;

import lombok.RequiredArgsConstructor;
import org.caesar.userservice.Data.Entities.Admin;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;

@RequiredArgsConstructor
public class AdminRepositoryImpl implements AdminRepository {

    //Oggetto per la comunicazione con keycloak
    private final Keycloak keycloak;

    //Definizione del Realm su cui operare
    private final RealmResource realmResource = keycloak.realm("CaesarRealm");

    //Oggetti per la presa dei dati degli utenti
    private final UsersResource usersResource = realmResource.users();
    private final UserRepresentation user = new UserRepresentation();

    @Override
    public Admin findUserById(String id) {

    }

    @Override
    public List<Admin> findAllUsers() {

    }

    @Override
    public Admin findAdminByEmail(String email) {

    }

    @Override
    public Admin findAdminByUsername(String username) {

    }
}
