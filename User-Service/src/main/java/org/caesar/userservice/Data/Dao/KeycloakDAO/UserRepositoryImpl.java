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



    @Override
    public List<String> findAllUsersByUsername(String username) {
        System.out.println("username: " + username);
        List<String> usernames = new ArrayList<>();
        try{
            System.out.println("sono nel try");
            RealmResource realmResource = keycloak.realm("CaesarRealm");
            List<UserRepresentation> users = realmResource.users().searchByUsername(username, false);
            System.out.println("lunghezza della lista: "+users.size());
            for (UserRepresentation user : users) {
                System.out.println("Inserisco gli utenti");
                //if(user.getAttributes().get("onChanges").getFirst().equals("true"))
                //    continue;
                System.out.println("aggiunta: "+user.getUsername());
                usernames.add(user.getUsername());
                System.out.println("Sono ancora nel for");
            }
            System.out.println("sono uscito dal for");
            for(String name: usernames)
                System.out.println("username: "+name);
            if(usernames.size() > 0)
                System.out.println("lista piena");
            else
                System.out.println("lista piena nulla");
            return usernames;
        }catch (Exception e) {
            log.debug("Errore nella presa di tutti gli utenti");
            System.out.println("dimensione lista: "+usernames.size());
            return null;
        }
    }

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
//                if(userRepresentation.getAttributes().get("onChanges").getFirst().equals("true"))
//                    continue;
                User user = new User();
                user.setId(userRepresentation.getId());
                user.setFirstName(userRepresentation.getFirstName());
                user.setLastName(userRepresentation.getLastName());
                user.setUsername(userRepresentation.getUsername());
                user.setEmail(userRepresentation.getEmail());
                //user.setPhoneNumber(String.valueOf(userRepresentation.getAttributes().get("phoneNumber")));

                result.add(user);
            }
        }

        return result;
    }



    @Override
    public User findUserByUsername(String username) {
        return setUser(false, username);
    }


    @Override
    public boolean logout(String username){
        try{
            RealmResource realmResource = keycloak.realm("CaesarRealm");
            User userKeycloak = findUserByUsername(username);
            UserResource userResource = realmResource.users().get(userKeycloak.getId());
            userResource.logout();
            return true;
        }catch (Exception | Error e) {
            log.debug("Errore nel logout");
            return false;
        }


    }


    //Metodi per la gestione dell'utente
    @Override
    @Transactional
    public boolean saveUser(UserRegistrationDTO userData) {
        System.out.println("Sono nel save user");
        // Presa del real associato all'applicazione
        RealmResource realmResource = keycloak.realm("CaesarRealm");

        // Presa degli utenti presenti sul real
        UsersResource usersResource = realmResource.users();

        // Creazione di un nuovo utente per inserirlo nel realm
        UserRepresentation user = new UserRepresentation();

        // Assegnazione dei campi base offerti da keycloak
        user.setUsername(userData.getUsername());
        user.setFirstName(userData.getFirstName());
        user.setLastName(userData.getLastName());
        user.setEmail(userData.getEmail());
        user.setEnabled(true);

        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put("phoneNumber", List.of("Inserisci un numero di telefono"));
        attributes.put("otp", List.of("null"));
        attributes.put("onChanges", List.of("false"));

        user.setAttributes(attributes);

        // Assegnazione e specifica del tipo di credenziali d'accesso
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(userData.getCredentialValue());
        credential.setTemporary(false);

        // Impostazione delle credenziali d'accesso
        user.setCredentials(Collections.singletonList(credential));

        // Chiamata per la creazione dell'user
        Response response = usersResource.create(user);


        // Controllo che l'user sia stato inserito
        if (response.getStatus() == 201) {
            System.out.println("L'user è stato inserito");
            // Presa dell'id dell'utente mandata come risposta della chiamata
            String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");

            UserResource userResource = usersResource.get(userId);

            // Impostazione del ruolo "basic" al nuovo utente salvato
            ClientRepresentation clientRepresentation = realmResource.clients().findByClientId("caesar-app").get(0);

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
        System.out.println("sono nella funziione per la modifica");
        try {
            System.out.println("sono nel try");
            //Presa del realm da keycloak per effettuare le operazioni in esso
            RealmResource realmResource = keycloak.realm("CaesarRealm");
            System.out.println("realm: "+realmResource);
            //Presa dell'id dell'utente e dell'utente stesso sull'interfaccia keycloak
            User userKeycloak = findUserByUsername(userData.getUsername());
            System.out.println("user: " + userData.getUsername()+ "firstname: " + userData.getFirstName());
            UserResource userResource = realmResource.users().get(userKeycloak.getId());
            System.out.println("resource: " + userResource.toRepresentation());
            System.out.println("sono piombato");
            //Aggiornamento dei dati dell'utente ad eccezione dell'username (attributo unique e non modificabiledisponibilita)
            UserRepresentation user = userResource.toRepresentation();
            user.setFirstName(userData.getFirstName());
            user.setLastName(userData.getLastName());
            user.setEmail(userData.getEmail());

            //Aggiunta degli attributi personalizzati

            System.out.println("prima di getAttributes");
            user.getAttributes().get("phoneNumber").set(0, userData.getPhoneNumber());
            user.getAttributes().get("onChanges").set(0, String.valueOf(userData.isOnChanges()));
            System.out.println("dopo get Attributes");
            boolean vb= userData.getOtp()!=null;
            if(userData.getOtp()!=null)
                user.getAttributes().get("otp").set(0, userData.getOtp());

            userResource.update(user);
            System.out.println("updatefatto");
            return true;
        } catch (Exception | Error e) {
            return false;
        }
    }


    @Override
    @Transactional
    public User validateOrRollbackDeleteUser(String username, boolean rollback) {
        try {

            //Presa dell'id dell'utente
            User user= findUserByUsername(username);

            //Presa dell'utente rappresentato attraverso l'interfaccia keycloak
            UserResource userResource= keycloak.realm("CaesarRealm").users().get(user.getId());
            UserRepresentation userRapr = userResource.toRepresentation();

            boolean vb= !rollback;
            userRapr.getAttributes().get("onChanges").set(0, String.valueOf(vb));

            userResource.update(userRapr);

            return user;
        } catch (Exception | Error e) {
            log.debug("Errore nella cancellazione dell'utente");
            return null;
        }
    }

    @Override
    public boolean releaseLockDeleteUser(String username) {
        try{
            //Presa dell'id dell'utente
            User user= findUserByUsername(username);

            //Presa dell'utente rappresentato attraverso l'interfaccia keycloak
            UserResource userResource= keycloak.realm("CaesarRealm").users().get(user.getId());

            userResource.remove();

            return true;
        }catch (Exception | Error e) {
            return false;
        }
    }


    @Override
    @Transactional
    public boolean changePassword(PasswordChangeDTO passwordChangeDTO, String username) {

        String userId= findUserByUsername(username).getId();

        if(userId==null)
            return false;

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
            UserRepresentation userRepresentation = usersResource.getFirst();
            if(userRepresentation==null)
                return null;

            User user = new User();

            user.setId(userRepresentation.getId());
            user.setFirstName(userRepresentation.getFirstName());
            user.setLastName(userRepresentation.getLastName());
            user.setUsername(userRepresentation.getUsername());
            user.setEmail(userRepresentation.getEmail());
            System.out.println("user: "+user.getUsername()+"firstname: "+user.getFirstName()+"lastname: "+user.getLastName());
            //Presa dei campi custom
            user.setPhoneNumber(userRepresentation.getAttributes().get("phoneNumber").getFirst());
            user.setOtp(userRepresentation.getAttributes().get("otp").getFirst());
            user.setOnChanges(Boolean.valueOf(userRepresentation.getAttributes().get("onChanges").getFirst()));
            System.out.println("problema non qui");

            return user;
        } catch (Exception | Error e) {
            log.debug(e.toString());
            return null;
        }
    }
}