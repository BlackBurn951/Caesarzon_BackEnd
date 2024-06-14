package org.caesar.userservice.Data.Services.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.userservice.Data.Dao.ProfilePicRepository;
import org.caesar.userservice.Data.Services.ProfilePicService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
@RequiredArgsConstructor
@Slf4j
public class ProfilePicServiceImpl implements ProfilePicService {

    private final ProfilePicRepository profilePicRepository;
    private final ModelMapper modelMapper;


    @Override
    public boolean saveImage(MultipartFile file) {
//        try {
//            ProfilePic profilePic = profilePicRepository.findByUserId(utils.getUserId().getUserId());
//            ProfilePicDTO imageProfile;
//
//            if (profilePic == null) {
//                imageProfile = new ProfilePicDTO();
//                imageProfile.setProfilePic(file.getBytes());
//                imageProfile.setUserId(utils.getUserId().getUserId());
//                profilePicRepository.save(modelMapper.map(imageProfile, ProfilePic.class));
//            }else{
//                profilePic.setProfilePic(file.getBytes());
//                profilePicRepository.save(profilePic);
//            }
//            return true;
//
//        }catch (IOException e){
//            e.printStackTrace();
//        }
//        return false;
        return true;
    }

    @Override
    public byte[] getUserImage(String username) {
        return profilePicRepository.findByUserUsername(username).getProfilePic();
    }

}