package com.basic.project.service;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.basic.project.entity.Profile;
import com.basic.project.entity.User;
import com.basic.project.repository.ProfileRepository;

@Service
public class ProfileService {
    @Autowired
    private ProfileRepository profileRepository; // Repository สำหรับ Entity Profile

    public void saveProfile(Profile profile) {
    	profileRepository.save(profile);
    }
    public Profile getProfileByUser(User user) {
        return profileRepository.findByUser(user);
    }
    public Profile getProfileById(int i) {
        return profileRepository.findById((long) i).orElse(null);
    }


}
