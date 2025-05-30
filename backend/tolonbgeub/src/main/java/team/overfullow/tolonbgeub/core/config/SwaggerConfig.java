package team.overfullow.tolonbgeub.core.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import team.overfullow.tolonbgeub.auth.AuthConfigProps;

import java.time.LocalDateTime;
import java.time.ZoneId;


@Configuration
@RequiredArgsConstructor
@OpenAPIDefinition(
        servers = {
                @Server(url = "https://i12a802.p.ssafy.io", description = "Dev-Server"),
                @Server(url = "http://127.0.0.1:8080", description = "Local-Server")
        }
)
public class SwaggerConfig {

    private final AuthConfigProps authConfigProps;

    @Bean
    public OpenAPI customOpenAPI() {
        System.out.println("authConfigProps.header = " + authConfigProps.header);
        SecurityScheme securityScheme = new SecurityScheme()
                .name(authConfigProps.header)
                .type(SecurityScheme.Type.HTTP)
                .in(SecurityScheme.In.HEADER)
                .scheme(authConfigProps.scheme)
                .bearerFormat("JWT");

        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("Bearer Token");

        return new OpenAPI()
                .components(new Components().addSecuritySchemes("Bearer Token", securityScheme))
                .addSecurityItem(securityRequirement)
                .info(new Info()
                        .title("Petstore API")
                        .version("v1:build at " + LocalDateTime.now(ZoneId.of("Asia/Seoul")))
                        .description(
                                """
                                        # [웹소켓 명세서로 이동](https://sisoya0424-1736226588737.atlassian.net/wiki/spaces/SSAFYA802/pages/11567105)
                                        
                                        우측하단 Authorize에 "test"를 입력하시면 자동으로 테스트 유저로 로그인됩니다.
                                        
                                        This is a sample server Petstore server. You can find out more about Swagger at [http://swagger.io](http://swagger.io) or on [irc.freenode.net, #swagger](http://swagger.io/irc/). For this sample, you can use the api key `special-key` to test the authorization filters.
                                        """
                        )
                        .termsOfService("http://swagger.io/terms/")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }
}