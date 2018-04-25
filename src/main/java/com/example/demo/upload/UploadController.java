package com.example.demo.upload;

 


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;


 
 
@Controller
public class UploadController {
 
	@Autowired
	StorageService storageService;
  
//	@GetMapping("/admin/upload")
//	public String uploadForm() {
//		return "uploadForm";
//	}
 
	@PostMapping("/api/admin/upload")
	@ResponseBody
	public String handleFileUploadApi(@RequestParam("file") MultipartFile theFile) throws FileNotImageExeption {
		String ms=null;
		if(isImage(theFile)) {
			try {
				ms=storageService.store(theFile);
			} catch (Exception e) {
				System.out.println("error in storing file : "+e.getMessage());
			}
		}else {
			throw new FileNotImageExeption();
		}
		return ms;
	}
	
	private boolean isImage(MultipartFile uploadedFile) {
		String mimeType=uploadedFile.getContentType();
		if (mimeType!=null && mimeType.startsWith("image/")) {
			return true;
		}
		return false;
	}
	

//	@PostMapping("/admin/upload")
//	public String handleFileUpload(@RequestParam("file") MultipartFile theFile, Map<String,String> model) {
//		String ms=handleFileUploadApi(theFile);
//		model.put("message", ms);
//		return "redirect:/admin/upload";
//	}
  
	@GetMapping("/api/files/{filename:.+}")
	@ResponseBody
	public ResponseEntity<Resource> getFile(@PathVariable String filename) {
		//if doesn't contain extension that means it isn't a file
		if(filename.contains(".")) {
			Resource file = storageService.loadFile(filename);
			System.out.println("fileName "+file.getFilename());
			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
					.body(file);
		}else {
			return null;
		}
	}
	
}