package digit.academy.tutorial.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI / Swagger configuration for Advocate Registry API.
 * Swagger UI: /advocate/swagger-ui/index.html (context-path is /advocate).
 * OpenAPI JSON: /advocate/v3/api-docs.
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.servlet.context-path:/advocate}")
    private String contextPath;

    @Bean
    public OpenAPI advocateRegistryOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Advocate Registry API")
                        .version("0.1.0")
                        .description("API for Advocate Registration (digit_assignment). Create, update, and search advocates per advocate-api-0.1.0.yaml.")
                        .contact(new Contact().name("DIGIT Academy")))
                .servers(List.of(new Server().url(contextPath).description("Advocate Registry")));
    }
}
