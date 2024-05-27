package com.etlv1.data;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

@SpringBootApplication
public class DataApplication {


	private static Log logger = LogFactory.getLog(DataApplication.class);

	@Bean
	public Function<String, String> reverse() throws ExecutionException, InterruptedException {

		return value -> {

			try {
				Execute e = new Execute();
				e.parallel();
			} catch (ExecutionException ex) {
				throw new RuntimeException(ex);
			} catch (InterruptedException ex) {
				throw new RuntimeException(ex);
			}

			logger.info("Processing and REVERSING: " + value);

			return new StringBuilder(value).reverse().toString();
		};
	}



	public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
		logger.info("==> Starting: DataApplication");


		if (!ObjectUtils.isEmpty(args)) {



			logger.info("==>  args: " + Arrays.asList(args));

		}
		SpringApplication.run(DataApplication.class, "--spring.cloud.function.routing-expression="
				+ "T(java.lang.System).nanoTime() % 2 == 0 ? 'REVERSING'");





	}

}
