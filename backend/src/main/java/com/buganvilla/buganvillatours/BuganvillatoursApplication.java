package com.buganvilla.buganvillatours;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.sql.Connection;

@SpringBootApplication
public class BuganvillatoursApplication {

	public static void main(String[] args) {
		SpringApplication.run(BuganvillatoursApplication.class, args);
	}
    @Bean
    CommandLineRunner testConnection(DataSource dataSource){
        return args -> {
            try (Connection conn = dataSource.getConnection()){
                System.out.println("Conexion a SQL Server exitosa");
            } catch (Exception e) {
                System.out.println("ERROR conectando a SQL Server");
                e.printStackTrace();
            }
        };
    }
}

