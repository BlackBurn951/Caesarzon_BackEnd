package org.caesar.userservice.GeneralService;

import lombok.RequiredArgsConstructor;
import org.caesar.userservice.Dto.TokenDTO;
import org.caesar.userservice.Dto.UserDTO;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;



@Service
@RequiredArgsConstructor
public class GeneralServiceImpl implements GeneralService {

    private final RestTemplate restTemplate;

    //Indirizzo per la registrazione dell'utente su keycloak
    private String tokenUrl= "http://localhost:8080/admin/realms/CaesarRealm/users";
    private String registrationUrl= "http://localhost:8080/admin/realms/CaesarRealm/users";

    //Dati di accesso per prendere il token keycloak
    @Value("${ADMIN_USER}")
    private String adminUser;

    @Value("${ADMIN_PASSWORD}")
    private String adminPassword;

    //{baseUrl}/admin/realms/{realm}/users/{userId} URI PER FARE LA CHIAMATA AL DB KEYCLOAK

    private TokenDTO getAdminToken() {
        //Creazione dell'header della chiamata http
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        System.out.println("Ho creato l'header!!!");
        //Creazione del body della chiamata http
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type","password");
        map.add("client_id","login-app");
        map.add("username",adminUser);
        map.add("password",adminPassword);

        System.out.println("Ho creato il body: "+ map.get("grant_type")+ "\n"+ map.get("client_id")+ "\n"+ map.get("username")+ "\n"+ map.get("password"));

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        System.out.println("Request: " + request.getBody());

        // Eseguire la richiesta POST e ottenere la risposta
        ResponseEntity<TokenDTO> response = restTemplate.postForEntity(tokenUrl, request, TokenDTO.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            // Ritorna il token DTO ottenuto dalla risposta
            return response.getBody();
        } else {
            // Gestisci eventuali errori di richiesta
            // Puoi lanciare un'eccezione o gestire diversamente l'errore
            return null; // o gestisci l'errore in base alle tue esigenze
        }
        
    }

    @Override
    public boolean saveUser(UserDTO userData) {
        TokenDTO adminToken= getAdminToken();
        if(adminToken!=null) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(adminToken.getAccess_token());


            HttpEntity<UserDTO> request = new HttpEntity<UserDTO>(userData, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(registrationUrl, request, String.class);

            return response.getStatusCode() == HttpStatusCode.valueOf(200);
        } else  {
            return false;
        }
    }
}
