package com.example.spring_spark_cassandra.repository;

import com.example.spring_spark_cassandra.model.UserActivity;
import org.springframework.data.cassandra.repository.CassandraRepository;

public interface UserActivityRepository extends CassandraRepository<UserActivity,String> {
}
