package com.example.crudapp.domain.product;

import com.example.crudapp.core.BaseController;
import com.example.crudapp.core.BaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController extends BaseController<Product> {

    private final ProductService productService;
    private final ProductFactory productFactory;

    @Override
    protected BaseService<Product> getService() {
        return productService;
    }

    @PostMapping("/dto")
    public Product createFromDto(@RequestBody ProductDTO dto) {
        Product product = productFactory.createEntity(dto);
        return productService.save(product);
    }
}
