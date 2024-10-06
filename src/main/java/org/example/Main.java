package org.example;

import oshi.SystemInfo;
import oshi.hardware.GlobalMemory;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main {
    public static void main(String[] args) {
        SystemInfo systemInfo = new SystemInfo();
        GlobalMemory memory = systemInfo.getHardware().getMemory();
        String computerName = System.getenv("COMPUTERNAME");

        //infinite loop checking ram usage and date every 30 seconds
        while(true) {
            //getting ram and converting to Mb
            long totalMemory = memory.getTotal();
            long availableMemory = memory.getAvailable();
            long usedMemory = (totalMemory - availableMemory)/(1024*1024);

            //getting current time and formatting it
            LocalDateTime currentDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = currentDateTime.format(formatter);

            System.out.println("Used memory: " + usedMemory + " MB || Date: " + formattedDateTime + " || PC name: " + computerName);

            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}