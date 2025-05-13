package com.example.spring_spark_cassandra.controller;

import com.example.spring_spark_cassandra.model.UserActivity;
import com.example.spring_spark_cassandra.repository.UserActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/activity")
public class UserActivityController {

    @Autowired
    UserActivityRepository userActivityRepository;

    @GetMapping("/job")
    public List<UserActivity> getAll() {
        return userActivityRepository.findAll();
    }


}
