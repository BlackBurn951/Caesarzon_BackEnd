package org.caesar.userservice.Data.Dao.KeycloakDAO;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.userservice.Data.Entities.Admin;
import org.caesar.userservice.Data.Entities.User;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Slf4j
public class AdminRepositoryImpl implements AdminRepository {

    //Oggetto per la comunicazione con keycloak
    private final Keycloak keycloak;

    @Override
    public List<Admin> findAllAdmin() {

        List<Admin> result = new ArrayList<>();

        RealmResource realmResource = keycloak.realm("CaesarRealm");

        List<UserRepresentation> admins = realmResource.users().list();

        // Ottieni il ClientRepresentation per il client "caesar-app"
        ClientRepresentation clientRepresentation = realmResource.clients().findByClientId("caesar-app").getFirst();
        String clientId = clientRepresentation.getId();

        for (UserRepresentation userRepresentation : admins) {
            // Ottieni i ruoli del client per l'utente
            List<RoleRepresentation> clientRoles = realmResource.users().get(userRepresentation.getId())
                    .roles()
                    .clientLevel(clientId)
                    .listEffective();

            // Verifica se l'utente ha il ruolo "admin"
            boolean hasAdminRole = clientRoles.stream()
                    .anyMatch(role -> role.getName().equals("admin"));

            if (hasAdminRole) {
                Admin admin = new Admin();
                admin.setId(userRepresentation.getId());
                admin.setFirstName(userRepresentation.getFirstName());
                admin.setLastName(userRepresentation.getLastName());
                admin.setUsername(userRepresentation.getUsername());
                admin.setEmail(userRepresentation.getEmail());
                result.add(admin);
            }
        }

        return result;
    }

    @Override
    @Transactional
    public int banUser(String username, boolean ban, boolean rollback) {  // 0 -> operazione riuscita 1 -> operazione già svolta in passato 2 -> errore
        RealmResource realmResource = keycloak.realm("CaesarRealm");
        try {
            //Presa dell'id dell'utente e dell'utente stesso sull'interfaccia keycloak
            User userKeycloak = findUserByUsername(username);
            UserResource userResource = realmResource.users().get(userKeycloak.getId());
            UserRepresentation user = userResource.toRepresentation();

            Map<String, List<String>> attributes = new HashMap<>();
            if(userKeycloak.isOnChanges()) {
                if(rollback) {
                    attributes.put("onChanges", List.of("false"));
                    user.setEnabled(!ban);

                    user.setAttributes(attributes);

                    userResource.update(user);
                }
                return 2;
            }

            if((!user.isEnabled() && ban) || (user.isEnabled() && !ban))
                return 1;

            user.setEnabled(!ban);

            attributes.put("onChanges", List.of("true"));
            user.setAttributes(attributes);

            userResource.update(user);
            return 0;
        } catch (Exception | Error e) {
            log.debug("Errore nel ban dell'user");
            return  2;
        }
    }

    @Override
    @Transactional
    public boolean completeBanUser(String username) {
        RealmResource realmResource = keycloak.realm("CaesarRealm");
        try {
            //Presa dell'id dell'utente e dell'utente stesso sull'interfaccia keycloak
            User userKeycloak = findUserByUsername(username);
            UserResource userResource = realmResource.users().get(userKeycloak.getId());
            UserRepresentation user = userResource.toRepresentation();

            Map<String, List<String>> attributes = new HashMap<>();
            attributes.put("onChanges", List.of("false"));

            userResource.update(user);
            return true;
        } catch (Exception | Error e) {
            log.debug("Errore nel ban dell'user");
            return  false;
        }
    }

    @Override
    public User findUserByUsername(String username) {
        return setUser(username);
    }

    @Override
    public List<User> findAllBanUsers(int start) {

        List<User> result = new ArrayList<>();

        RealmResource realmResource = keycloak.realm("CaesarRealm");

        List<UserRepresentation> users = realmResource.users().list(start, 20);

        // Ottieni il ClientRepresentation per il client "caesar-app"
        ClientRepresentation clientRepresentation = realmResource.clients().findByClientId("caesar-app").getFirst();
        String clientId = clientRepresentation.getId();

        for (UserRepresentation userRepresentation : users) {

            if (!userRepresentation.isEnabled()) {
                // Ottieni i ruoli del client per l'utente
                List<RoleRepresentation> clientRoles = realmResource.users().get(userRepresentation.getId())
                        .roles()
                        .clientLevel(clientId)
                        .listEffective();

                // Verifica se l'utente ha il ruolo "basic"
                boolean hasBasicRole = clientRoles.stream()
                        .anyMatch(role -> role.getName().equals("basic"));

                if (hasBasicRole) {
                    if(userRepresentation.getAttributes().get("onChanges").getFirst().equals("true"))
                        continue;
                    User user = new User();
                    user.setId(userRepresentation.getId());
                    user.setFirstName(userRepresentation.getFirstName());
                    user.setLastName(userRepresentation.getLastName());
                    user.setUsername(userRepresentation.getUsername());
                    user.setEmail(userRepresentation.getEmail());
                    user.setPhoneNumber(String.valueOf(userRepresentation.getAttributes().get("phoneNumber")));

                    result.add(user);
                }
            }
        }

        return result;
    }

    //Metodi di servizio
    private User setUser(String field) {  //Metodo per costruire l'oggetto entity
        try {

            //Presa del realm da keycloak per effettuare le operazioni in esso
            RealmResource realmResource = keycloak.realm("CaesarRealm");

            //Scaricamento dei singoli utenti presenti su keycloak
            List<UserRepresentation> usersResource;

            //Ricerca del singolo utente attraverso username
            usersResource = realmResource.users().searchByUsername(field, true);

            UserRepresentation userRepresentation = usersResource.getFirst();
            if(userRepresentation==null)
                return null;

            //Creazione dell'ggetto entity
            User user = new User();

            user.setId(userRepresentation.getId());
            user.setFirstName(userRepresentation.getFirstName());
            user.setLastName(userRepresentation.getLastName());
            user.setUsername(userRepresentation.getUsername());
            user.setEmail(userRepresentation.getEmail());

            user.setPhoneNumber(userRepresentation.getAttributes().get("phoneNumber").getFirst());
            user.setOtp(userRepresentation.getAttributes().get("otp").getFirst());
            user.setOnChanges(Boolean.valueOf(userRepresentation.getAttributes().get("onChanges").getFirst()));

            return user;
        } catch (Exception | Error e) {
            log.debug("Errore nella costruzione dell'user da keycloak");
            return null;
        }
    }
}