package prj.cognitive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import kong.unirest.GsonObjectMapper;
import kong.unirest.Unirest;

@SpringBootApplication
public class Cognitive {
	public static void main(String[] args) throws Exception {
		Gson gson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();
		Unirest.config().setObjectMapper(new GsonObjectMapper(gson));

		// WebServer server = new WebServer();
		SpringApplication.run(Cognitive.class, args);
	}
}
