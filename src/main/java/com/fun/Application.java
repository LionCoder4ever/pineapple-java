package com.fun;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
public class Application {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            log.error("Usage: java -jar your_pineapplefile_absolute_path");
            return;
        }
        String filePath = args[0];
        List<String> fileContent = Files.readAllLines(Paths.get(filePath));
        String code = String.join("\n\r", fileContent);
        new Backend().execute(code);
    }
}
