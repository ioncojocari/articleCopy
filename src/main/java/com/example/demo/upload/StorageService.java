package com.example.demo.upload;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;




@Service
@Configuration
public class StorageService {
	
	//String sRootPath = new File("").getAbsolutePath();
	//java.io.tmpdir
	
	//	public static final  String sRootPath="/opt/tomcat/uploads/articleCopyUploads";
	public static final  String sRootPath="D:/temp";
	private final Path rootLocation = Paths.get(sRootPath);
	private Random random=new Random();
	/***
	 * Return new file name;
	 * @param file
	 * @return
	 */
	public String store(MultipartFile file){
		init();
		String fileName;
		boolean fileExits=exists(file.getOriginalFilename());
		if(!fileExits) {
			fileName=getNewFileName(file.getOriginalFilename());
		}else {
			fileName=file.getOriginalFilename();
		}
		try {
            Files.copy(file.getInputStream(), this.rootLocation.resolve(fileName));
        } catch (Exception e) {
        	throw new RuntimeException("FAIL!");
        }
		return fileName;
	}

    public Resource loadFile(String filename) {
        try {
            Path file = rootLocation.resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            if(resource.exists() || resource.isReadable()) {
                return resource;
            }else{
            	throw new RuntimeException("FAIL!");
            }
        } catch (MalformedURLException e) {
        	throw new RuntimeException("FAIL!");
        }
    }
    
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

    public void init() {
    	File file=new File(sRootPath);
    	boolean exists=file.exists();
    	if(! exists) {
	        try {
	            Files.createDirectory(rootLocation);
	        } catch (IOException e) {
	            throw new RuntimeException("Could not initialize storage!path : "+sRootPath+" " +e .getLocalizedMessage());
	        }
    	}
    }
    
    private boolean exists(String fileName) {
    	File file=new File(sRootPath+"/"+fileName);
    	if(file.exists()) {
    		return false;
    	}
    	return true;
    }
    
    private String getNewFileName(String originalName) {
    	String newName= random.nextInt(100000)+originalName;
    	if(!exists(newName)) {
    		return getNewFileName(originalName);
    	}else {
    		return newName;
    	}
    }
    
    public void deleteAllFiles()  {
    	try {
    		FileUtils.cleanDirectory(new File(sRootPath)); 
    	}catch(Exception e) {
    		System.out.println(e.getMessage());
    	}
    }
    
    public void deleteFile(String fileName) {
    	File file=new File(sRootPath+"/"+fileName);
    	System.out.println("file path : "+file.getAbsolutePath());
    	if(file.exists()) {
    		System.out.println("file exists");
    		file.delete();
    	}
    }
}
