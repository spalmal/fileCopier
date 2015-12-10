package com.test;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.Callable;

/**
 * @author damart1
 * 
 * Callable is choosen instead of Runnable incase in the future if you want to return a value after copy operation.
 *
 */
public class FileCopier implements Callable<Integer> {
	private FileData fileData;
	
	public FileCopier(FileData fileData) {
		this.fileData = fileData;
	}

	
	@Override
	public Integer call() throws Exception {

		int returnValue = -1;
		try {
            WatchService watcher = FileSystems.getDefault().newWatchService();
            Path dir = Paths.get(fileData.getSrcdir());
            dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
             
            System.out.println("Watch Service registered for dir: " + dir.getFileName());
            infinteloop: 
            while (true) {
                WatchKey key=null;
                try {
                    key = watcher.take();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                 
                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();
                     
                    @SuppressWarnings("unchecked")
                    WatchEvent<Path> ev = (WatchEvent<Path>) event;
                    Path fileName = ev.context();
                     
                    System.out.println(kind.name() + ": " + fileName);
                     
                    if (kind == ENTRY_MODIFY &&
                            fileName.toString().equals(fileData.getDestfile())) {
                    	
            			Path srcPath=Paths.get(fileData.getSrcdir(), fileData.getSrcfile());
            			Path destPath=Paths.get(fileData.getDestdir(),fileData.getDestfile());
            			Files.copy(srcPath, destPath ,StandardCopyOption.REPLACE_EXISTING);
            			BasicFileAttributes attributes = Files.readAttributes(srcPath,
            					BasicFileAttributes.class);
            			//TODO: check today's date here and do necessary changes.
            			System.out.println(attributes.lastModifiedTime());

                        System.out.println("My source file has changed!!!");
                        returnValue = 0;
                        break infinteloop;
                    }
                }
                 
                boolean valid = key.reset();
                if (!valid) {
                    break;
                }
            }
             
        } catch (IOException ex) {
            System.err.println(ex);
        }
		return returnValue;
	}
	
	

}
