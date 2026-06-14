package com.jade.marketplace.s3;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.jade.marketplace.product.Product;
import com.jade.marketplace.product.ProductImage;
import com.jade.marketplace.product.ProductRepository;
import com.jade.marketplace.product.ProductService;

import jakarta.transaction.Transactional;

/**
 * Service to upload product images
 * 
 * Responsibilities:
 * Validate uploaded image
 * Upload image file to S3
 * Save image URL on Product entity
 */
@Service
public class ImageUploadService {

    private final S3Service s3Service;
    private final ProductService productService;
    private final ProductRepository productRepository;

    /**
     * Constructor
     */
    public ImageUploadService(S3Service s3Service, ProductService productService, ProductRepository productRepository) {
        this.s3Service = s3Service;
        this.productService = productService;
        this.productRepository = productRepository;
    }

    /**
     * Uploads an image for a product and attaches the image URL to that product
     * 
     * MultipartFile = Spring Object for a file uploaded from an HTTP request
     * Then with that object file, we can do:
     * file.getBytes() = actual image bytes
     * file.getOriginalFileName() ="milu.png"
     * file.getContentType() = "image/png"
     * file.isEmpty() = "true/false"
     */
    @Transactional
    public Product uploadProductImage(Long productId, MultipartFile file) {

        // check if image file is empty
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Image file is required");
        }

        // check if image type is invalid (rejects PDFs, text files, etc.)
        if (file.getContentType() == null || !file.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("Uploaded file must be an image");
        }

        // get product this image file belongs to
        Product product = productService.findById(productId);

        try {
            // build image key / file name
            String imageKey = s3Service.buildProductImageKey(productId, file.getOriginalFilename());

            // upload image bytes to S3
            s3Service.uploadBytes(imageKey, file.getBytes(), file.getContentType());

            // build image URL
            String imageUrl = s3Service.buildObjectUrl(imageKey);

            // add image to product entity
            product.addImage(new ProductImage(product, imageUrl));

            // return saved product into database
            return productRepository.save(product);
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to read image file", e);
        }
    }
    
}
