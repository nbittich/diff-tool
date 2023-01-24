package lblod.info.datasetdiff;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import mu.semte.ch.lib.config.CoreConfig;

@SpringBootApplication
@Import(CoreConfig.class)
public class DiffToolApplication {

	public static void main(String[] args) {
		SpringApplication.run(DiffToolApplication.class, args);
	}

}
