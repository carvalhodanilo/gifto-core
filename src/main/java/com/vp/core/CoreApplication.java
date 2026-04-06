package com.vp.core;

import com.vp.core.infrastructure.config.KeycloakAdminProperties;
import com.vp.core.infrastructure.config.storage.S3StorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({ S3StorageProperties.class, KeycloakAdminProperties.class })
public class CoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(CoreApplication.class, args);
	}

}
