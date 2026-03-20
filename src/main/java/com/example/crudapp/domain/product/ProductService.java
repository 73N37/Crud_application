package com.example.crudapp.domain.product;

import com.example.crudapp.core.BaseRepository;
import com.example.crudapp.core.BaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService extends BaseService<Product> {

    private final ProductRepository productRepository;

    @Override
    protected BaseRepository<Product> getRepository() {
        return productRepository;
    }
}
