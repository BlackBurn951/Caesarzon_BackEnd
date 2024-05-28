package org.caesar.userservice.GeneralService;

import lombok.RequiredArgsConstructor;
import org.caesar.userservice.Dto.UserDTO;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class GeneralServiceImpl implements GeneralService {

    private final Keycloak keycloak;


    @Override
    public boolean saveUser(UserDTO userData) {
        RealmResource realmResource = keycloak.realm("CaesarRealm");
        UsersResource usersResource = realmResource.users();

        UserRepresentation user = new UserRepresentation();
        user.setUsername(userData.getUsername());
        user.setFirstName(userData.getFirstName());
        user.setLastName(userData.getLastName());
        user.setEmail(userData.getEmail());
        user.setEnabled(true);



        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put("numeroTelefono", Collections.singletonList("3497276241"));
        user.setAttributes(attributes);

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(userData.getCredentialValue());
        credential.setTemporary(false);

        user.setCredentials(Collections.singletonList(credential));


        Response response = usersResource.create(user);
        if (response.getStatus() == 201) {
            System.out.println("User created successfully");
            return true;
        } else {
            System.out.println("Error creating user: " + response.getStatusInfo().getReasonPhrase());
            return false;
        }
    }
}
