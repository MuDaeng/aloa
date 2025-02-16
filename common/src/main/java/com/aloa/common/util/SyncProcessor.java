package com.aloa.common.util;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Component
public class SyncProcessor {
    public void startProcess(ProcessBuilder pb, String processName) throws IOException, InterruptedException {
        pb.redirectErrorStream(true);
        Process process = pb.start();

        try (BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
        }

        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new RuntimeException(processName + " failed with exit code " + exitCode);
        }
    }
}
