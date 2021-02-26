package uz.alvasti.yozma.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableSwagger2
@PropertySource("classpath:swagger.properties")
public class SwaggerConfig {

    private static final Logger logger = LogManager.getLogger("ServiceLogger");

    List<ResponseMessage> list = new ArrayList<>();

    @Value("${swagger.client.access-token-uri}")
    private String authLink;

    public SwaggerConfig() {

    }

    @Bean
    public Docket productApi() {
        list.add(new ResponseMessageBuilder().code(500).message("500 message").responseModel(new ModelRef("Result"))
                .build());
        list.add(new ResponseMessageBuilder().code(401).message("Unauthorized").responseModel(new ModelRef("Result"))
                .build());
        list.add(new ResponseMessageBuilder().code(406).message("Not Acceptable").responseModel(new ModelRef("Result"))
                .build());

        return new Docket(DocumentationType.SWAGGER_2)
                .host(authLink.substring(authLink.indexOf("//")+2))
                .select()
                .apis(RequestHandlerSelectors.any()).paths(PathSelectors.any()).build()
                .useDefaultResponseMessages(false).apiInfo(apiInfo()).globalResponseMessage(RequestMethod.GET, list)
                .globalResponseMessage(RequestMethod.POST, list);

    }

    private ApiInfo apiInfo() {
        return new ApiInfo("Simple rest api", "", "", "", new Contact("Created By  Alvasti Group",
                "https://yozma.alvatry.uz/", "javateam@yozma.uz"), "", "", Collections.emptyList());
    }

}
