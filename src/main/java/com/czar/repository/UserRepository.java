package com.czar.repository;

import com.czar.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface UserRepository extends JpaRepository<User,Long> {

    List<User> findByPasswordAndLoginNameAndDomain(String password, String loginName,String domain);


}
