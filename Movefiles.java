package com.ad2pro.spectra.dir.watchService.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;

@Component
class Movefiles {


    //will run as soon as spring applicatio starts
    @PostConstruct
    private static void folderWatcher() {

        try {

            // java nio librarey supports methods to do file opperations
            // watches directory for changes 
            WatchService watcher = FileSystems.getDefault().newWatchService();

            Path dir = Paths.get("Path to the folder");

            // subscribe for events ENTRY_CREATE, ENTRY_DELETE etc
            dir.register(watcher, ENTRY_CREATE);
            System.out.println("Watch Service registered for dir: " + dir.getFileName());
            WatchKey key;
            while ((key = watcher.take()) != null) {

                // pollevents to moniter the folder
                for (WatchEvent<?> event : key.pollEvents()) {

                    WatchEvent.Kind<?> kind = event.kind();

                    @SuppressWarnings("unchecked")
                    WatchEvent<Path> ev = (WatchEvent<Path>) event;
                    Path fileName = ev.context();

                    // do somthing based on event
                    if (kind == ENTRY_CREATE) {
                        System.out.println("New File Added, file Name " + fileName);
                        //example to move file to another directory
                        moveFile(fileName);
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

    public static void moveFile(Path fileName) throws IOException {
        Path tDir = Paths.get("path to destination");
        Path sDir = Paths.get("sorce directory");

        System.out.println("Inetiating transfer "+ tDir);

        // construct path fith new file
        String newFilePath = tDir + File.separator + fileName;
        System.out.println("File "+ fileName);
        String sourceFilePath = sDir + File.separator + fileName;
        System.out.println("Destination dir "+ newFilePath);
        Files.move(Paths.get(sourceFilePath), Paths.get(newFilePath), StandardCopyOption.REPLACE_EXISTING);

        System.out.println("Done !!!");
    }
}
