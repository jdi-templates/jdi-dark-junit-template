package com.epam.jdi.httptests.example;

import com.epam.http.requests.RequestDataFactory;
import com.epam.http.requests.RestMethods;
import com.epam.http.response.RestResponse;
import org.junit.jupiter.api.*;

import static com.epam.http.requests.ServiceInit.init;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Example of endpoint tests with setting request data on-fly
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ServiceTests {

    private ServiceExample service;

    @BeforeAll
    public void before() {
        service = init(ServiceExample.class);
    }

    @Test
    public void simpleRestTest() {
        RestResponse resp = ServiceExample.getInfo.call();
        resp.isOk().
                body("url", equalTo("https://httpbin.org/get")).
                body("headers.Host", equalTo("httpbin.org")).
                body("headers.Id", equalTo("Test"));
        resp.assertThat().header("Connection", "keep-alive");
    }

    @Test
    public void noServiceObjectTest() {
        RestResponse resp = RestMethods.GET(RequestDataFactory.requestData(rd -> {
                    rd.uri = "https://httpbin.org/get";
                    rd.headerUpdater().addAll(new Object[][]{
                            {"Name", "Roman"},
                            {"Id", "TestTest"}
                    });
                }
        ));
        resp.isOk().header("Connection", "keep-alive");
        resp.assertBody(new Object[][]{
                {"url", equalTo("https://httpbin.org/get")},
                {"headers.Host", equalTo("httpbin.org")},
                {"headers.Id", equalTo("TestTest")}
        });
    }

    @Test
    public void statusTestWithQueryInPath() {
        RestResponse resp = service.statusWithQuery.pathParams("503", "some").call();
        assertEquals(resp.getStatus().code, 503);
        resp.isEmpty();
    }

    @Test
    public void retryRequestTest() {
        service.status.call(RequestDataFactory.pathParams().add("status", "500"))
                .assertThat()
                .statusCode(500);
    }

    @Test
    public void serviceInitTest() {
        RestResponse resp = service.postMethod.call();
        resp.isOk().assertThat().
                body("url", equalTo("https://httpbin.org/post")).
                body("headers.Host", equalTo("httpbin.org"));
    }

    @Test
    public void htmlBodyParseTest() {
        RestResponse resp = service.getHTMLMethod.call();
        resp.isOk();
        assertEquals(resp.getFromHtml("html.body.h1"), "Herman Melville - Moby-Dick");
    }

    @Test
    public void cookiesTest() {
        RestResponse response = service.getCookies.call(RequestDataFactory.cookies().add("additionalCookie", "test"));
        response.isOk()
                .body("cookies.additionalCookie", equalTo("test"))
                .body("cookies.session_id", equalTo("1234"))
                .body("cookies.hello", equalTo("world"));
    }

}
