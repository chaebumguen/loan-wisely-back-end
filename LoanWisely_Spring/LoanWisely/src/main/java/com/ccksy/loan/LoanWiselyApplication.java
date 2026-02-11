package com.ccksy.loan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class LoanWiselyApplication {

	private static final Logger log = LoggerFactory.getLogger(LoanWiselyApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(LoanWiselyApplication.class, args);
	}

	@Bean
	ApplicationRunner logDbContext(DataSource dataSource) {
		return args -> {
			try (Connection conn = dataSource.getConnection();
			     PreparedStatement ps = conn.prepareStatement(
				     "select " +
				     "sys_context('USERENV','CON_NAME') as con_name, " +
				     "sys_context('USERENV','SERVICE_NAME') as service_name, " +
				     "sys_context('USERENV','CURRENT_SCHEMA') as current_schema " +
				     "from dual");
			     ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					log.info("DB CON_NAME={} SERVICE_NAME={} CURRENT_SCHEMA={}",
						rs.getString("con_name"),
						rs.getString("service_name"),
						rs.getString("current_schema"));
				}
			}
		};
	}
}
