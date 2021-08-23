package com.lucy.test;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.config.SSLConfig;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ApiTest {

    private String baseUrl = "";
    private String expectCityName = "";

    private Properties loadFromEnvProperties(String propFileName) {
        Properties prop = null;


        try {
            String path = ApiTest.class.getClassLoader().getResource(propFileName).getPath();
            prop = new Properties();
            System.out.println(path);
            InputStream in = new BufferedInputStream(new FileInputStream(path));
            prop.load(in);
            in.close();
        } catch (Exception e) {
            System.out.println("配置文件加载失败，请检查" + File.separator + propFileName);
        }
        return prop;
    }

    @Step
    private String getCityName(String cityCode) {

        String fullUrl = baseUrl + cityCode + ".html";
        Response resp = RestAssured
                .given()
                .config(RestAssured.config().sslConfig(new SSLConfig().relaxedHTTPSValidation()))
                .get(fullUrl);
        resp.print();
        return resp.jsonPath().get("weatherinfo.city");
    }

    @BeforeEach
    public void begin() {
        String propFileName = "interface.properties";
        Properties prop = loadFromEnvProperties(propFileName);
        String host = prop.getProperty("server_addr", "www.weather.com.cn");
        baseUrl = "http://" + host + "/data/cityinfo/";

    }

    @AfterEach
    public void tearDown() {
        System.out.println(expectCityName + "Test Finished");
    }

    @Test
    @Feature("Test ShenZhen")
    public void testShenZhen() {
        expectCityName = "深圳";
        String actualCityName = getCityName("101280601");
        Assertions.assertEquals(expectCityName, actualCityName);
    }

    @Test
    @Feature("Test ShangHai")
    public void testShangHai() {
        expectCityName = "上海";
        String actualCityName = getCityName("101020100");
        Assertions.assertEquals(expectCityName, actualCityName);
    }

    @Test
    @Feature("Test Beijing")
    public void testBeijing() {
        expectCityName = "北京";
        String actualCityName = getCityName("101010100");
        Assertions.assertEquals(expectCityName, actualCityName);
    }


}
