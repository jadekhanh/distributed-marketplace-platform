package com.jade.marketplace.s3;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.jade.marketplace.product.Product;

/**
 * Controller for product image uploads
 * 
 * Note: use REST for file uploads because multipart file handling is simpler than GraphQL file upload in Spring
 */
@RestController
@RequestMapping("/products")
public class ImageUploadController {

    private final ImageUploadService imageUploadService;

    /**
     * Constructor
     */
    public ImageUploadController(ImageUploadService imageUploadService) {
        this.imageUploadService = imageUploadService;
    }

    /**
     * Uploads an image for a product and attaches image url to that product
     */
    @PostMapping("/{productId}/images")
    public Product uploadProductImage(@PathVariable Long productId, @RequestParam("file") MultipartFile file) {
        return imageUploadService.uploadProductImage(productId, file);
    }
}
