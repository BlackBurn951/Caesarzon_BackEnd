package org.caesar.userservice.Data.Dao.KeycloakDAO;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.userservice.Data.Entities.User;

import org.caesar.userservice.Dto.*;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import org.springframework.stereotype.Component;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserRepositoryImpl implements UserRepository {

    private final Keycloak keycloak;



    //Metodi per la ricerca dell'utente
    @Override
    public User findUserById(String id) {
        RealmResource realmResource = keycloak.realm("CaesarRealm");
        UserResource userResource = realmResource.users().get(id);

        UserRepresentation userRepresentation = userResource.toRepresentation();

        User user = new User();

        user.setId(userRepresentation.getId());
        user.setFirstName(userRepresentation.getFirstName());
        user.setLastName(userRepresentation.getLastName());
        user.setUsername(userRepresentation.getUsername());
        user.setEmail(userRepresentation.getEmail());
        user.setPhoneNumber(String.valueOf(userRepresentation.getAttributes().get("phoneNumber")));

        return user;
    }

    @Override
    public List<String> findAllUsersByUsername(String username) {

        List<String> usernames = new ArrayList<>();
        try{
            System.out.printf("sono nel findAllUsersByUsername: %s\n", username);
            RealmResource realmResource = keycloak.realm("CaesarRealm");
            List<UserRepresentation> users = realmResource.users().searchByUsername(username, false);

            for (UserRepresentation user : users) {
                usernames.add(user.getUsername());
            }
            return usernames;
        }catch (Exception e) {
            System.out.printf("Errore: %s\n", e.getMessage());
            return null;
        }
    } //TODO CHECK

    //Metodo per prendere tutti gli utenti "basic" dal real (20 alla volta)
    @Override
    public List<User> findAllUsers(int start) {

        List<User> result = new ArrayList<>();

        RealmResource realmResource = keycloak.realm("CaesarRealm");

        List<UserRepresentation> users = realmResource.users().list(start, 20);

        // Ottieni il ClientRepresentation per il client "caesar-app"
        ClientRepresentation clientRepresentation = realmResource.clients().findByClientId("caesar-app").getFirst();
        String clientId = clientRepresentation.getId();

        for (UserRepresentation userRepresentation : users) {
            // Ottieni i ruoli del client per l'utente
            List<RoleRepresentation> clientRoles = realmResource.users().get(userRepresentation.getId())
                    .roles()
                    .clientLevel(clientId)
                    .listEffective();

            // Verifica se l'utente ha il ruolo "basic"
            boolean hasBasicRole = clientRoles.stream()
                    .anyMatch(role -> role.getName().equals("basic"));

            if (hasBasicRole) {
                User user = new User();
                user.setId(userRepresentation.getId());
                user.setFirstName(userRepresentation.getFirstName());
                user.setLastName(userRepresentation.getLastName());
                user.setUsername(userRepresentation.getUsername());
                user.setEmail(userRepresentation.getEmail());
                if (userRepresentation.getAttributes() != null) {
                    user.setPhoneNumber(String.valueOf(userRepresentation.getAttributes().get("phoneNumber")));
                }
                result.add(user);
            }
        }

        return result;
    }



    @Override
    public User findUserByEmail(String email) {
        return setUser(true, email);
    }

    @Override
    public User findUserByUsername(String username) {
        return setUser(false, username);
    }


    //Metodi per la gestione dell'utente
    @Override
    @Transactional
    public boolean saveUser(UserRegistrationDTO userData) {
        //Presa del real associato all'applicazione
        RealmResource realmResource = keycloak.realm("CaesarRealm");

        //Presa degli utenti presenti sul real
        UsersResource usersResource = realmResource.users();

        //Creazione di un nuovo utente per inserirlo nel realm
        UserRepresentation user = new UserRepresentation();

        System.out.println(userData.getCredentialValue());
        System.out.println(userData.getUsername());
        System.out.println(userData.getFirstName());
        System.out.println(userData.getLastName());
        System.out.println(userData.getEmail());

        //Assegnazione dei campi base offerti da keycloak
        user.setUsername(userData.getUsername());
        user.setFirstName(userData.getFirstName());
        user.setLastName(userData.getLastName());
        user.setEmail(userData.getEmail());
        user.setEnabled(true);

        //Assegnazione e specifica del tipo di crednziali d'accesso
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(userData.getCredentialValue());
        credential.setTemporary(false);

        //Impostazione delle credenziali d'accesso
        user.setCredentials(Collections.singletonList(credential));

        //Chiamata per la creazione dell'user
        Response response = usersResource.create(user);

        //Controllo che l'user sia stato inserito
        if (response.getStatus() == 201) {
            //Presa dell'id dell'utente mandata come risposta della chiamata
            String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");

            UserResource userResource = usersResource.get(userId);

            //Impostazione del ruolo "basic" al nuovo utente salvato
            ClientRepresentation clientRepresentation= realmResource.clients().findByClientId("caesar-app").getFirst();
            ClientResource clientResource = realmResource.clients().get(clientRepresentation.getId());

            RoleRepresentation role = clientResource.roles().get("basic").toRepresentation();
            userResource.roles().clientLevel(clientRepresentation.getId()).add(Collections.singletonList(role));

            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean updateUser(UserDTO userData) {
        try {
            //Presa del realm da keycloak per effettuare le operazioni in esso
            RealmResource realmResource = keycloak.realm("CaesarRealm");

            //Presa dell'id dell'utente e dell'utente stesso sull'interfaccia keycloak
            User userKeycloak = findUserByUsername(userData.getUsername());
            UserResource userResource = realmResource.users().get(userKeycloak.getId());

            //Aggiornamento dei dati dell'utente ad eccezione dell'username (attributo unique e non modificabile)
            UserRepresentation user = new UserRepresentation();
            user.setFirstName(userData.getFirstName());
            user.setLastName(userData.getLastName());
            user.setEmail(userData.getEmail());

            //Aggiunta degli attributi personalizzati
            Map<String, List<String>> attributes = new HashMap<>();  //FIXME controllare vecchia config

            attributes.put("phoneNumber", List.of(userData.getPhoneNumber()));
            user.setAttributes(attributes);

            userResource.update(user);

            //Controllo che il campo email sia cambiato, se si invio dell'email di verifica
            if (!userKeycloak.getEmail().equals(userData.getEmail()))
                userResource.sendVerifyEmail();

            return true;
        } catch (Exception | Error e) {
            log.debug("Errore nella fase di aggiornamento dell'utente su keycloak");
            return false;
        }
    }

    @Override
    @Transactional
    public boolean banUser(String username, boolean ban) {
        RealmResource realmResource = keycloak.realm("CaesarRealm");
        log.debug("Nella repository dell'user prima di prendere gli utenti");
        try {
            //Presa dell'id dell'utente e dell'utente stesso sull'interfaccia keycloak
            User userKeycloak = findUserByUsername(username);
            UserResource userResource = realmResource.users().get(userKeycloak.getId());

            log.debug("Dopo aver preso l'user");
            UserRepresentation user = userResource.toRepresentation();
            user.setEnabled(!ban);
            log.debug("Disabilitato");
            userResource.update(user);
            log.debug("Chiamata effettuata");
            return true;
        } catch (Exception | Error e) {
            log.debug("Errore nel ban dell'user");
            return  false;
        }
    }

    @Override
    @Transactional
    public boolean deleteUser(String username) {

        //Presa dell'id dell'utente
        String userId= findUserByUsername(username).getId();

        //Presa dell'utente rappresentato attraverso l'interfaccia keycloak
        UserResource userResource= keycloak.realm("CaesarRealm").users().get(userId);

        try {
            userResource.remove();
        } catch (Exception | Error e) {
            log.debug("Errore nella cancellazione dell'utente");
            return false;
        }

        return true;
    }

    @Override
    public boolean changePassword(PasswordChangeDTO passwordChangeDTO, String username) {

        String userId= findUserByUsername(username).getId();
        UserResource userResource= keycloak.realm("CaesarRealm").users().get(userId);

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setTemporary(false);
        credential.setValue(passwordChangeDTO.getPassword());

        userResource.resetPassword(credential);
        return true;

    }


    //Metodi di servizio
    private User setUser(boolean type, String field) {  //Metodo per costruire l'oggetto entity
        try {

            //Presa del realm da keycloak per effettuare le operazioni in esso
            RealmResource realmResource = keycloak.realm("CaesarRealm");

            //Scaricamento dei singoli utenti presenti su keycloak
            List<UserRepresentation> usersResource;

            //Ricerca del singolo utente attraverso uno dei attributi scelti
            if (type) {
                usersResource = realmResource.users().searchByEmail(field, true);
            } else {
                usersResource = realmResource.users().searchByUsername(field, true);
            }

            //Creazione dell'ggetto entity
            User user = new User();

            user.setId(usersResource.getFirst().getId());
            user.setFirstName(usersResource.getFirst().getFirstName());
            user.setLastName(usersResource.getFirst().getLastName());
            user.setUsername(usersResource.getFirst().getUsername());
            user.setEmail(usersResource.getFirst().getEmail());

            //Verifica ed eventuale aggiunta del campo inerente al numero di telefono
            if (usersResource.getFirst().getAttributes() != null)
                user.setPhoneNumber(usersResource.getFirst().getAttributes().get("phoneNumber").getFirst());

            return user;
        } catch (Exception | Error e) {
            log.debug("Errore nella costruzione dell'user da keycloak");
            return null;
        }
    }
}
