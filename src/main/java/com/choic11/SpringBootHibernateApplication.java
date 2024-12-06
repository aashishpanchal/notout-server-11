package com.choic11;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.sql.DataSource;

@SpringBootApplication(exclude = HibernateJpaAutoConfiguration.class)
@EnableScheduling
public class SpringBootHibernateApplication extends SpringBootServletInitializer  implements CommandLineRunner{

	@Autowired
    DataSource dataSource;

	
	public static void main(String[] args) {
		SpringApplication.run(SpringBootHibernateApplication.class, args);
	}
	
	
	 @Override
    public void run(String... args) throws Exception {

        System.out.println("DATASOURCE = " + dataSource);

    }
}
