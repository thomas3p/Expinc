package com.basic.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.basic.project.entity.Profile;
import com.basic.project.entity.User;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

	Profile findByUser(User user);
    // สามารถเพิ่มเมทอดเพิ่มเติมในกรณีที่ต้องการสำหรับการค้นหาหรือจัดการข้อมูล Profile
}
