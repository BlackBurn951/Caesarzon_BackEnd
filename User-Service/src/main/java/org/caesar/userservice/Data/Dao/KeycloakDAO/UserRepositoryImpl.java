package org.caesar.userservice.Data.Dao.KeycloakDAO;

import lombok.RequiredArgsConstructor;
import org.caesar.userservice.Config.JwtConverter;
import org.caesar.userservice.Data.Entities.User;
import org.caesar.userservice.Dto.PhoneNumberDTO;
import org.caesar.userservice.Dto.UserDTO;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
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

    public User setUser(boolean type, String field) {
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
        user.setPhoneNumber(String.valueOf(usersResource.getFirst().getAttributes().get("phoneNumber")));

        return user;
    }

    //Converter per il token
    private final JwtConverter jwtConverter = new JwtConverter();


    private UserRepository userRepository;

    @Override
    public boolean saveUser(UserDTO userData) {
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
        String username = jwtConverter.getUsernameFromToken();
        UsersResource usersResource = realmResource.users();

        boolean response;

        try {
            //Ricerca dell'utente tramite username
            List<UserRepresentation> users =  usersResource.searchByUsername(username, true);

            UserRepresentation user = users.getFirst();

            //Impostazione dell'attributo phoneNumber
            Map<String, List<String>> attributes = new HashMap<>();
            attributes.put("phoneNumber", Collections.singletonList(phoneNumberDTO.getPhone_number()));
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
