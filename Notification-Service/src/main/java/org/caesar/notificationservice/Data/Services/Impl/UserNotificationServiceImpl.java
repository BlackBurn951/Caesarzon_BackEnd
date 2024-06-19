package org.caesar.notificationservice.Data.Services.Impl;

import lombok.RequiredArgsConstructor;
import org.caesar.notificationservice.Data.Dao.UserNotificationRepository;
import org.caesar.notificationservice.Data.Entities.UserNotification;
import org.caesar.notificationservice.Data.Services.UserNotificationService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UserNotificationServiceImpl implements UserNotificationService {

    private final UserNotificationRepository userNotificationRepository;
    private final ModelMapper modelMapper;

    @Override
    public boolean addUserNotification(String username, String description, String explanation) {
        UserNotification notification = new UserNotification();

        notification.setUser(username);
        notification.setDescription(description);
        notification.setDate(LocalDate.now());
        notification.setRead(false);
        notification.setExplanation(explanation);

        try{
            userNotificationRepository.save(modelMapper.map(notification, UserNotification.class));
            return true;
        }catch(Exception | Error e){
           e.printStackTrace();
           return false;
        }
    }
}
