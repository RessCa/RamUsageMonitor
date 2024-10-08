package org.example;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import oshi.SystemInfo;
import oshi.hardware.GlobalMemory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Main {

    private static final String API_URL = "http://localhost:8080/";

    public static void main(String[] args) {
        SystemInfo systemInfo = new SystemInfo();
        GlobalMemory memory = systemInfo.getHardware().getMemory();
        String computerName = System.getenv("COMPUTERNAME");

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            //infinite loop checking ram usage every 30 seconds
            while (true) {
                long totalMemory = memory.getTotal();
                long availableMemory = memory.getAvailable();
                long usedMemory = (totalMemory - availableMemory) / (1024 * 1024);

                String jsonHttpRequest = String.format("{\"ramUsage\": %d, \"computerName\": \"%s\"}", usedMemory, computerName);

                sendDataToAPI(httpClient, jsonHttpRequest);

                try {
                    Thread.sleep(30000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void sendDataToAPI(CloseableHttpClient httpClient, String jsonHttpRequest) {
        HttpPost postRequest = new HttpPost(API_URL);
        postRequest.setHeader("Content-Type", "application/json");

        StringEntity entity = new StringEntity(jsonHttpRequest, StandardCharsets.UTF_8);
        postRequest.setEntity(entity);

        try (CloseableHttpResponse response = httpClient.execute(postRequest)) {
            int statusCode = response.getCode();
            if(statusCode != 200) {
                System.out.println("ERROR Server code:" + statusCode);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}