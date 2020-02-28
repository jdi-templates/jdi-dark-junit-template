package com.epam.jdi.httptests.example;

import com.epam.http.requests.ServiceSettings;
import com.epam.http.requests.errorhandler.ErrorHandler;
import com.epam.http.response.ResponseStatusType;
import com.epam.http.response.RestResponse;
import com.epam.jdi.httptests.example.dto.Organization;
import com.epam.jdi.httptests.utils.TrelloDataGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.authentication.BasicAuthScheme;
import io.restassured.internal.mapping.Jackson2Mapper;
import io.restassured.mapper.ObjectMapper;
import io.restassured.path.json.mapper.factory.Jackson2ObjectMapperFactory;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.lang.reflect.Type;

import static com.epam.http.requests.ServiceInit.init;
import static com.epam.http.response.ResponseStatusType.CLIENT_ERROR;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Example of using common settings for all service endpoints
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ServiceSettingsTests {

    private ServiceSettings serviceSettings;
    private ErrorHandler errorHandler;
    private ObjectMapper objectMapper;
    private RequestSpecification requestSpecification;
    private BasicAuthScheme authenticationScheme;

    //Setup error handler for processing unexpected responses
    private void initErrorHandler() {
        errorHandler = new ErrorHandler() {
            @Override
            public boolean hasError(RestResponse restResponse) {
                //only Client errors will be caught
                return ResponseStatusType.getStatusTypeFromCode(restResponse.getStatus().code) == CLIENT_ERROR;
            }

            @Override
            public void handleError(RestResponse restResponse) {
                throw new RuntimeException("Exception is caught: " + restResponse.toString());
            }
        };
    }

    //Setup object mapper for custom serialization/deserialization responses from/to objects
    private void initObjectMapper() {
        objectMapper = new Jackson2Mapper(new Jackson2ObjectMapperFactory() {
            @Override
            public com.fasterxml.jackson.databind.ObjectMapper create(Type type, String s) {
                com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                return objectMapper;
            }
        });
    }

    //Setup request settings which will send for all requests
    private void initRequestSpecification() {
        requestSpecification = given().filter(new AllureRestAssured());
        requestSpecification.header("Header1", "Value1");
    }

    //Setup authorization scheme
    private void initAuthScheme() {
        authenticationScheme = new BasicAuthScheme();
        authenticationScheme.setUserName("user");
        authenticationScheme.setPassword("password");
    }

    @BeforeAll()
    public void initService() {
        initErrorHandler();
        initObjectMapper();
        initRequestSpecification();
        initAuthScheme();
        serviceSettings = ServiceSettings.builder()
                .errorHandler(errorHandler)
                .objectMapper(objectMapper)
                .requestSpecification(requestSpecification)
                .authenticationScheme(authenticationScheme)
                .build();
        init(TrelloService.class, serviceSettings);
    }

    @Test
    public void assignBoardToOrganization() {
        assertThrows(RuntimeException.class, () -> {
            assignBoard();
        });
    }

    private void assignBoard() {
        //Create organization
        Organization organization = TrelloDataGenerator.generateOrganization();
        organization.setName(null);
        organization.setDisplayName(null);
        //this endpoint causes 400 error
        TrelloService.createOrganization(organization);
    }
}
