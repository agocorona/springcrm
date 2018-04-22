package crmapi;



import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import org.springframework.web.bind.annotation.RestController;



import crmapi.storage.StorageFileNotFoundException;
import crmapi.storage.StorageService;

import org.springframework.util.StringUtils;

import java.security.Principal;


@RestController
public class FileUploadController {

    private final StorageService storageService;
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;


    @Autowired
    public FileUploadController(StorageService storageService
                               ,CustomerRepository customerRepository
                               ,AccountRepository accountRepository) {
        this.storageService = storageService;
        this.customerRepository= customerRepository;
        this.accountRepository= accountRepository;
    }

    // @GetMapping("/images")
    // public String listUploadedFiles(Model model) throws IOException {

    //     model.addAttribute("files", storageService.loadAll().map(
    //             path -> MvcUriComponentsBuilder.fromMethodName(FileUploadController.class,
    //                     "serveFile", path.getFileName().toString()).build().toString())
    //             .collect(Collectors.toList()));

    //     return "uploadForm";
    // }
    // @GetMapping("/images")
    // List<String>  listUploadedFiles(Model model) throws IOException {

    //     return storageService.loadAll().map(
    //                path ->path.getFileName().toString()).stream().collect(Collectors.toList()));

        
    // }

    @GetMapping("/images/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @PostMapping("/images/upload")
    @Transactional
    public void handleImageUpload(Principal principal,@RequestParam("file") MultipartFile file
           ,@RequestParam("customer") String customer) {
        validateUserAdmin(principal);
        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        Integer i= filename.lastIndexOf('.');
        String ext= filename.substring(i);
        
        String name= customer+ext;
        storageService.store(name,file);
        Customer customerReg = customerRepository.findByName(customer).orElseThrow(
                                     () -> new UserNotFoundException(customer));
        // String photoPath= MvcUriComponentsBuilder.fromMethodName(FileUploadController.class,
        //                      "serveFile", name).build().toString();
        customerReg.setPhoto(name);
		customerRepository.save(customerReg);

        
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }
    private void validateUserAdmin(Principal principal) {
		if (principal!= null){
			String userId = principal.getName();
			Account acc= this.accountRepository
						.findByUsername(userId).orElseThrow(
							() -> new UserNotFoundException(userId));
						
			Boolean isAdmin= acc.getIsAdmin();
			if (!isAdmin) throw( new AccessDeniedException(userId));
		}
	}

}