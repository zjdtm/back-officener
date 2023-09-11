package fastcampus.team7.Livable_officener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class LivableOfficenerApplication {

	public static void main(String[] args) {
		SpringApplication.run(LivableOfficenerApplication.class, args);
	}

}
