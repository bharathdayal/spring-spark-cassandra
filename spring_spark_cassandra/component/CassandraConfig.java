package com.example.spring_spark_cassandra.component;

import com.datastax.oss.driver.api.core.CqlSession;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

@Configuration
public class CassandraConfig {

    @Value("${cassandra.contact-point}")
    private String contactPoint;

    @Value("${cassandra.port}")
    private int port;

    @Value("${cassandra.local-datacenter}")
    private String datacenter;

    @Bean
    public CqlSession cassandraSession() {
        return CqlSession.builder()
                .addContactPoint(new InetSocketAddress(contactPoint, port))
                .withLocalDatacenter(datacenter)
                .withKeyspace("user_analytics")
                .build();
    }

    @Bean
    public SparkSession sparkSession() {
        SparkConf conf = new SparkConf()
                .setAppName("spring-spark-cassandra")
                .setMaster("local[*]")
                .set("spark.cassandra.connection.host", contactPoint)
                .set("spark.cassandra.connection.port", String.valueOf(port))
                .set("spark.cassandra.connection.localDC", datacenter)
                .set("spark.sql.codegen.wholeStage", "false");

        SparkSession spark = SparkSession.builder()
                .config(conf)
                .getOrCreate();


        Dataset<Row> df = spark.read().option("multiline",true).json("src/main/resources/activity.json");


        Dataset<Row> badRows = df.filter("user_id IS NULL");
        if (!badRows.isEmpty()) {
            System.out.println("Bad rows with null user_id:");
            badRows.show(); // or log to file
        }

        Dataset<Row> validRows = df.filter("user_id IS NOT NULL");
        validRows.show();

        Dataset<Row> summary = df.groupBy("user_id")
                .agg(functions.count("*").alias("login_count"),
                        functions.max("timestamp").alias("last_activity"));

        summary.write()
                .format("org.apache.spark.sql.cassandra")
                .option("keyspace", "user_analytics")
                .option("table", "activity_summary")
                .mode(SaveMode.Append)
                .save();

        spark.stop();
        return spark;
    }
}
