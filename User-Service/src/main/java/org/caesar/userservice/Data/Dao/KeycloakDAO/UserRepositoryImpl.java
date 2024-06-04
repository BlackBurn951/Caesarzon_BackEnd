package org.caesar.userservice.Data.Dao.KeycloakDAO;

import lombok.RequiredArgsConstructor;
import org.caesar.userservice.Config.JwtConverter;
import org.caesar.userservice.Data.Entities.User;
import org.caesar.userservice.Dto.PhoneNumberDTO;
import org.caesar.userservice.Dto.UserRegistrationDTO;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import java.util.*;

@Component
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    //Converter per il token
    private final JwtConverter jwtConverter = new JwtConverter();

    private UserRepository userRepository;

    //Oggetti per la comunicazione con keycloak
    private final Keycloak keycloak;


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

    public List<User> findAllUsers() {
        RealmResource realmResource = keycloak.realm("CaesarRealm");

        List<User> result = new ArrayList<>();

        //Prendiamo tutti gli utenti del realm (CaesarzonRealm)
        UsersResource usersResource = realmResource.users();

        //Convertiamo tutti gli utenti in UserRepresentation per accedere ai dati dei singoli utenti
        List<UserRepresentation> users = usersResource.list();

        //Foreach sugli utenti, filtriamo per id e raccogliamo tutti i ruoli dei singoli utenti
        for (UserRepresentation userRepresentation : users) {
            UserResource userResource = usersResource.get(userRepresentation.getId());

            //Raccolta della lista di ruoli dell'utente
            List<RoleRepresentation> roles = userResource.roles().clientLevel("caesar-app").listEffective();

            //Se l'utente possiede il ruolo "basic" lo aggiungiamo alla nostra lista di utenti (mappando)
            for (RoleRepresentation role : roles) {
                if (role.getName().equals("basic")) {
                    User user = new User();
                    user.setId(userRepresentation.getId());
                    user.setFirstName(userRepresentation.getFirstName());
                    user.setLastName(userRepresentation.getLastName());
                    user.setUsername(userRepresentation.getUsername());
                    user.setEmail(userRepresentation.getEmail());
                    user.setPhoneNumber(String.valueOf(userRepresentation.getAttributes().get("phoneNumber")));
                    result.add(user);
                    break;
                }
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

    public User setUser(boolean type, String field) {
        RealmResource realmResource = keycloak.realm("CaesarRealm");

        List<UserRepresentation> usersResource;

        if(type){
            usersResource = realmResource.users().searchByEmail(field, true);
        }else{
            usersResource = realmResource.users().searchByUsername(field, true);
        }

        User user = new User();

        user.setId(usersResource.getFirst().getId());
        user.setFirstName(usersResource.getFirst().getFirstName());
        user.setLastName(usersResource.getFirst().getLastName());
        user.setUsername(usersResource.getFirst().getUsername());
        user.setEmail(usersResource.getFirst().getEmail());
        if(usersResource.getFirst().getAttributes() != null)
            user.setPhoneNumber(String.valueOf(usersResource.getFirst().getAttributes().get("phoneNumber")));

        return user;
    }


    @Override
    public boolean saveUser(UserRegistrationDTO userData) {
        RealmResource realmResource = keycloak.realm("CaesarRealm");

        UsersResource usersResource = realmResource.users();

        UserRepresentation user = new UserRepresentation();

        user.setUsername(userData.getUsername());
        user.setFirstName(userData.getFirstName());
        user.setLastName(userData.getLastName());
        user.setEmail(userData.getEmail());
        user.setEnabled(true);


        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(userData.getCredentialValue());
        credential.setTemporary(false);

        user.setCredentials(Collections.singletonList(credential));


        Response response = usersResource.create(user);
        if (response.getStatus() == 201) {
            String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
            UserResource userResource = usersResource.get(userId);
            userResource.sendVerifyEmail();
            System.out.println("User created successfully");
            return true;
        } else {
            System.out.println("Error creating user: " + response.getStatusInfo().getReasonPhrase());
            return false;
        }
    }

    @Override
    public boolean savePhoneNumber(PhoneNumberDTO phoneNumberDTO) {
        RealmResource realmResource = keycloak.realm("CaesarRealm");
        String username = jwtConverter.getUsernameFromToken();
        UsersResource usersResource = realmResource.users();

        boolean response;

        try {
            //Ricerca dell'utente tramite username
            List<UserRepresentation> users =  usersResource.searchByUsername(username, true);

            UserRepresentation user = users.getFirst();

            //Impostazione dell'attributo phoneNumber
            Map<String, List<String>> attributes = new HashMap<>();
            attributes.put("phoneNumber", Collections.singletonList(phoneNumberDTO.getPhoneNumber()));
            user.setAttributes(attributes);

            //Verifica se l'attributo è stato impostato correttamente
            response =  user.getAttributes() != null && user.getAttributes().containsKey("phoneNumber");

            System.out.println("Attributi:" + user.getAttributes());
        } catch (Exception e) {
            e.printStackTrace();
            response = false;
        }
        return response;
    }
}
