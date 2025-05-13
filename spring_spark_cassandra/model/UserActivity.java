package com.example.spring_spark_cassandra.model;

import lombok.Data;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.Instant;

@Data
@Table("activity_summary")
public class UserActivity {

    @PrimaryKey("user_id")
    private String userId;

    private int loginCount;

    private Instant lastActivity;
}
