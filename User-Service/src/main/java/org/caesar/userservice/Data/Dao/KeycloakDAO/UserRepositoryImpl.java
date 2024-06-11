package org.caesar.userservice.Data.Dao.KeycloakDAO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.userservice.Config.JwtConverter;
import org.caesar.userservice.Data.Dao.ProfilePicRepository;
import org.caesar.userservice.Data.Entities.ProfilePic;
import org.caesar.userservice.Data.Entities.User;

import org.caesar.userservice.Dto.PhoneNumberDTO;
import org.caesar.userservice.Dto.ProfilePicDTO;
import org.caesar.userservice.Dto.UserDTO;
import org.caesar.userservice.Dto.UserRegistrationDTO;
import org.caesar.userservice.Utils.Utils;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.modelmapper.ModelMapper;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserRepositoryImpl implements UserRepository {

    //Converter per il token
    private final JwtConverter jwtConverter = new JwtConverter();

    private final ProfilePicRepository profilePicRepository;

    private final ModelMapper modelMapper;

    //Oggetti per la comunicazione con keycloak
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

    @Override
    public String getUserIdFromToken() {
        return this.findUserByUsername(jwtConverter.getUsernameFromToken()).getId();
    }


    //Metodi per la gestione dell'utente
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
        log.debug("Ho impostato i dati dell'utente");
        Response response = usersResource.create(user);
        log.debug("Ho creato l'utente");
        if (response.getStatus() == 201) {
            String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
            UserResource userResource = usersResource.get(userId);


            //userResource.sendVerifyEmail();


            ProfilePicDTO profilePic = new ProfilePicDTO();
            File file = new File("User-Service/src/main/resources/static/img/base_profile_pic.jpg");

            try{
                MultipartFile multipartFile = new MockMultipartFile("file", file.getName(), "image/jpeg", Files.readAllBytes(file.toPath()));

                profilePic.setProfilePic(multipartFile.getBytes());
            }catch (IOException e){
                e.printStackTrace();
            }

            profilePic.setUserId(userId);

            profilePicRepository.save(modelMapper.map(profilePic, ProfilePic.class));

            ClientRepresentation clientRepresentation= realmResource.clients().findByClientId("caesar-app").getFirst();
            ClientResource clientResource = realmResource.clients().get(clientRepresentation.getId());

            RoleRepresentation role = clientResource.roles().get("basic").toRepresentation();
            userResource.roles().clientLevel(clientRepresentation.getId()).add(Collections.singletonList(role));

            log.debug("Ho ricevuto risposta 200 da KEY");

            return true;
        } else
            return false;
    }




    @Override
    public boolean updateUser(UserDTO userData) {
        RealmResource realmResource = keycloak.realm("CaesarRealm");

        User userKeycloak= findUserByUsername(jwtConverter.getUsernameFromToken());
        UserResource userResource = realmResource.users().get(userKeycloak.getId());

        UserRepresentation user = new UserRepresentation();
        user.setFirstName(userData.getFirstName());
        user.setLastName(userData.getLastName());
        user.setEmail(userData.getEmail());

        Map<String, List<String>> attributes = user.getAttributes() != null
                ? user.getAttributes() : new HashMap<>();
        attributes.put("phoneNumber", List.of(userData.getPhoneNumber()));
        user.setAttributes(attributes);

        userResource.update(user);

        if(!userKeycloak.getEmail().equals(userData.getEmail()))
            userResource.sendVerifyEmail();
        return true;
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

        } catch (Exception e) {
            e.printStackTrace();
            response = false;
        }
        return response;
    }

    @Override
    public boolean deleteUser(String username) {
        String userId= findUserByUsername(username).getId();

        UserResource userResource= keycloak.realm("CaesarRealm").users().get(userId);

        log.debug("Nella repository user prima del delete");
        try {
            userResource.remove();
        } catch (Exception e) {
            log.debug("Errore nella cancellazione dell'utente");
            return false;
        }
        log.debug("Nella repository user dopo la delete");

        return true;
    }


    //Metodi di servizio
    private User setUser(boolean type, String field) {
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
            user.setPhoneNumber(usersResource.getFirst().getAttributes().get("phoneNumber").get(0));

        return user;
    }
}
