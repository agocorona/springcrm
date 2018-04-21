package crmapi;

import java.io.IOException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.net.URI;



import crmapi.storage.StorageFileNotFoundException;
import crmapi.storage.StorageService;

@RestController
public class FileUploadController {

    private final StorageService storageService;
    private final CustomerRepository customerRepository;


    @Autowired
    public FileUploadController(StorageService storageService
                               ,CustomerRepository customerRepository) {
        this.storageService = storageService;
        this.customerRepository= customerRepository;
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

    @PostMapping("/images")
    public void handleImageUpload(@RequestParam("file") MultipartFile file
           ,@RequestParam("customer") String customer) {
       
        storageService.store(customer,file);
        Customer customerReg = customerRepository.findByName(customer).orElseThrow(
                                     () -> new UserNotFoundException(customer));
        String photoPath= MvcUriComponentsBuilder.fromMethodName(FileUploadController.class,
                             "serveFile", customer+".img").build().toString();
		customerRepository.save(new Customer(customerReg.getName(),customerReg.getSurname(),photoPath));

        
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }

}