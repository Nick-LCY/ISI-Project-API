package com.group3.apiserver.controller;

import com.group3.apiserver.dto.PaginationDTO;
import com.group3.apiserver.dto.ProductDetailDTO;
import com.group3.apiserver.dto.ProductListItemDTO;
import com.group3.apiserver.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
@CrossOrigin("http://localhost:8080")
public class ProductController {
    private ProductService productService;

    @Autowired
    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

    /**
     * order_by:
     *  1: ascending
     *  0: descending
     */
    @GetMapping("/products")
    public PaginationDTO<ProductListItemDTO> getProductList(@RequestParam(name = "page", defaultValue = "1") Integer page,
                                                            @RequestParam(name = "key", defaultValue = "") String key,
                                                            @RequestParam(name = "category", defaultValue = "____") String category,
                                                            @RequestParam(name = "order_by", defaultValue = "1") Integer orderBy) {
        return productService.findProducts(page, key, category, orderBy);
    }

    @GetMapping("/product")
    public ProductDetailDTO getProduct(@RequestParam(name = "id") Integer id) {
        return productService.findProduct(id);
    }

    @GetMapping("/testPage")
    public String testPage() {
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "\t<title>Test</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "<form action=\"http://localhost:9981/upload_example\" method=\"post\" enctype=\"multipart/form-data\">\n" +
                "\t<input type=\"file\" name=\"file1\">\n" +
                "\t<input type=\"file\" name=\"file2\">\n" +
                "\t<button type=\"submit\">Rua</button>\n" +
                "</form>\n" +
                "</body>\n" +
                "</html>";
    }

    @PostMapping("/upload_example")
    public String test(@RequestParam("file1") MultipartFile multipartFile1, @RequestParam("file2") MultipartFile multipartFile2) {
        try {
            ResourceUtils.getFile("classpath:static");
            Resource fileResource = new ClassPathResource("static");
            File dir = fileResource.getFile();
            String addr = dir.getAbsolutePath() + "\\" + multipartFile1.getOriginalFilename();
            Files.write(Paths.get(addr), multipartFile1.getBytes());
            return "<a href=\"/static/" + multipartFile1.getOriginalFilename() + "\">Check It!</a>";
        } catch (IOException e) {
            e.printStackTrace();
            return "fail";
        }
    }
}
