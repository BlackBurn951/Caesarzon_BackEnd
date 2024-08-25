package org.caesar.userservice.Data.Services.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.userservice.Data.Dao.KeycloakDAO.UserRepository;
import org.caesar.userservice.Data.Entities.User;
import org.caesar.userservice.Data.Services.UserService;
import org.caesar.userservice.Dto.*;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    //Metodo per prendere i dati di un'utente
    @Override
    public UserDTO getUser(String username) {
        try {
            // Conversione dell'oggetto entity in un oggetto DTO per poi privarlo dell'id per non farlo girare sulla rete
            UserDTO userDTO = modelMapper.map(userRepository.findUserByUsername(username), UserDTO.class);
            userDTO.setId("");

            return userDTO;
        } catch (Exception | Error e) {
            log.debug("Errore nella presa dei dati dell'utente");
            return null;
        }
    }

    //Metodo per restituire tutti gli utenti
    @Override
    public List<UserDTO> getUsers(int start) {
        List<User> users= userRepository.findAllUsers(start);

        List<UserDTO> result= new Vector<>();

        for (User user : users) {
            result.add(modelMapper.map(user, UserDTO.class));
        }

        return result;
    }

    //Metodo per prendere la lista di utenti
    @Override
    public List<String> getUsersByUsername(String username) {
        return userRepository.findAllUsersByUsername(username);
    }

    //Metodo per salvare un utente
    @Override
    public boolean saveUser(UserRegistrationDTO userRegistrationDTO) {
        //Controllo che i campi mandati da front non siano null e che rispettino il formato richiesto
        if(checkUsername(userRegistrationDTO.getUsername()) &&
            checkEmail(userRegistrationDTO.getEmail()) &&
            checkFirstName(userRegistrationDTO.getFirstName()) &&
            checkLastName(userRegistrationDTO.getLastName()) &&
            checkCredentialValue(userRegistrationDTO.getCredentialValue()))
            return userRepository.saveUser(userRegistrationDTO);
        return false;
    }

    //Metodo per aggiornare i dati di un utente
    @Override
    public boolean updateUser(UserDTO userDTO) {
        //Controllo che i campi mandati da front non siano null e che rispettino il formato richiesto
        if(checkUsername(userDTO.getUsername()) &&
                checkEmail(userDTO.getEmail()) &&
                checkFirstName(userDTO.getFirstName()) &&
                checkLastName(userDTO.getLastName()) &&
                checkPhoneNumber(userDTO.getPhoneNumber()))
            return userRepository.updateUser(userDTO);
        return false;
    }

    //Metodo per eliminare un utente
    @Override
    public boolean deleteUser(String userUsername) {
        try {
            return userRepository.validateOrRollbackDeleteUser(userUsername);
        } catch(Exception | Error e) {
            log.debug("Errore nella cancellazione dell'utente");
            return false;
        }
    }

    //Metodo per cambiare la password di un utente
    @Override
    public boolean changePassword(PasswordChangeDTO passwordChangeDTO, String username) {
        try {
            return userRepository.changePassword(passwordChangeDTO, username);
        } catch(Exception | Error e) {
            log.debug("Errore nella cancellazione dell'utente");
            return false;
        }
    }

    @Override
    public boolean checkOtp(OtpDTO otp) {
        User user= userRepository.findUserByUsername(otp.getUsername());

        if(user==null)
            return false;

        if(user.getOtp().equals(otp.getOtp())) {
            user.setOtp(null);
            return userRepository.updateUser(modelMapper.map(user, UserDTO.class));
        }
        return false;
    }

    @Override
    public boolean logout(String usermame, LogoutDTO logoutDTO) {
        if(logoutDTO.isLogout())
            return userRepository.logout(usermame);
        return false;
    }


    //Metodi per la convalida dei dati
    private boolean checkUsername(String username) {
        //Controllo che l'username non sia meno lungo di 5 caratteri e non più lungo di 20 contenente solo caratteri e numeri
        return username!=null && (username.length()>=5 && username.length()<=20) &&
                (username.matches("^(?=.*[a-zA-Z].*[a-zA-Z].*[a-zA-Z].*[a-zA-Z])[a-zA-Z0-9]{5,20}$"));
    }

    private boolean checkEmail(String email) {
        if(email==null)
            return false;

        try {
            //Lettura da file dei domini prefissati
            List<String> domains = Files.readAllLines(Path.of("User-Service/src/main/resources/static/domains.txt"));
            int atIndex = email.indexOf("@");

            //Suddivisione in pre e post @ dell'email per controllare i due singoli pezzi
            String beforeAt = email.substring(0, atIndex);
            String afterAt = email.substring(atIndex + 1);
            //Check del before per fare in modo che non sia più lungo di 64 caratteri e non contenga caratteri speciali
            boolean checkBefore= beforeAt.matches("^[a-zA-Z0-9.]{1,64}$");

            if(checkBefore) {
                for(String domain : domains) {
                    if(afterAt.contains(domain))
                        return true;
                }
            }
        } catch(IOException e) {
            log.debug("Errore nell'apertura del file dei domini accettati");
            return false;
        }
        return false;
    }

    private boolean checkFirstName(String firstName) {
        //Controllo che il nome non nullo, sia meno lungo di 2 caratteri e non più lungo di 30 contenente solo caratteri e numeri
        return firstName!=null && (firstName.length()>=2 && firstName.length()<=30) &&
                (firstName.matches("^[a-zA-Z]{2,}([a-zA-Z]{2,30})?$"));
    }

    private boolean checkLastName(String lastName) {
        //Controllo che il nome  non sia meno lungo di 2 caratteri e non più lungo di 30 contenente solo caratteri e numeri
        return lastName!=null && (lastName.length()>=2 && lastName.length()<=30) &&
                (lastName.matches("^[a-zA-Z]{2,}( [a-zA-Z]{2,30})?$"));
    }

    private boolean checkCredentialValue(String credentialvalue) {
        //Controllo che la password non sia nulla, meno lunga di 8 caratteri e non più lunga di 20 contenente caratteri speciali e numeri
        return credentialvalue!=null && (credentialvalue.length()>=8 && credentialvalue.length()<=20) &&
                (credentialvalue.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+])[A-Za-z\\d!@#$%^?&*()_+]{8,20}$"));
    }

    private boolean checkPhoneNumber(String phoneNumber) {
        //Controllo che il numero di telefono non sia nullo, contenga solo numeri e sia lungo esattamente 10 caratteri
        return phoneNumber!=null && phoneNumber.matches("[0-9]{10}");
    }

}
