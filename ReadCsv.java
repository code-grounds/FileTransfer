package com.arjun.watchService;

import org.jetbrains.annotations.NotNull;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;

@Component
public class ReadCsv {

    List<Path> csvLis = new ArrayList<>();



// moniter folder for changes
    @PostConstruct()
    void getCsvForProcessing(){
        System.out.println("Delayed "+2 * 1000);
        try {
            WatchService watchProcessing = FileSystems.getDefault().newWatchService();

            Path dir = Paths.get("processing folder");
            dir.register(watchProcessing, ENTRY_CREATE);
            System.out.println("Metadata Service registered for dir: " + dir.getFileName());
            WatchKey key;
            while ((key = watchProcessing.take()) != null) {
                for (WatchEvent<?> event : key.pollEvents()) {

                    WatchEvent.Kind<?> kind = event.kind();

                    @SuppressWarnings("unchecked")
                    WatchEvent<Path> ev = (WatchEvent<Path>) event;
                    Path fileName = ev.context();

                    if (kind == ENTRY_CREATE) {
                        System.out.println("New File Added in Processing dir " + fileName.endsWith("csv"));

// get csv file 
                        if (fileName.toString().endsWith("csv")){
                            // create hole path to file
                            String absoluteFilePath = dir + File.separator + fileName;
                            this.csvLis.add(Paths.get(absoluteFilePath));
                            processMetadata(Paths.get(absoluteFilePath));
                        }
                        //fileNameToDb(fileName);
                    }
                }

                boolean valid = key.reset();
                if (!valid) {
                    break;
                }
            }
        }
        catch (IOException | InterruptedException ex) {
            System.err.println(ex);
        }
    }

// print data in csv to console
    void processMetadata(@NotNull Path fileName) {
        try (BufferedReader reader = Files.newBufferedReader(fileName.toRealPath(), Charset.forName("UTF-8"))) {
            String currentLine = null;
            while ((currentLine = reader.readLine()) != null) {//while there is content on the current line
                System.out.println(currentLine); // print the current line
            }
        } catch (IOException ex) {
            ex.printStackTrace(); //handle an exception here
        }
    }
}
