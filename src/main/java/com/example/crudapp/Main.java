package com.example.crudapp;

import com.example.crudapp.api.JavalinUniversalController;
import com.example.crudapp.infrastructure.persistence.HibernateUtil;
import com.example.crudapp.infrastructure.security.JwtInterceptor;
import com.example.crudapp.logic.DynamicCrudManager;
import com.example.crudapp.domain.product.ProductInterceptor;
import com.example.crudapp.data.Product;
import io.javalin.Javalin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        log.info("Starting Generic CRUD Application (Vanilla Java)...");

        // 🔧 Generate a test token for development
        String testSecret = "your-very-secure-and-long-secret-key-for-jwt-validation";
        SecretKey key = io.jsonwebtoken.security.Keys.hmacShaKeyFor(testSecret.getBytes(StandardCharsets.UTF_8));
        String testToken = io.jsonwebtoken.Jwts.builder()
                .subject("test-user")
                .signWith(key)
                .compact();
        log.info("--------------------------------------------------");
        log.info("🔑 TEST JWT TOKEN (for Postman/Browser extension):");
        log.info("Bearer " + testToken);
        log.info("--------------------------------------------------");

        // 1. Initialize logic components
        DynamicCrudManager crudManager = new DynamicCrudManager();
        
        // Register interceptors manually (since we don't have DI)
        crudManager.registerInterceptor(Product.class, new ProductInterceptor());

        // 2. Discover and register resources from package
        crudManager.discoverAndRegister("com.example.crudapp.data");

        // 3. Initialize Controller
        JavalinUniversalController controller = new JavalinUniversalController(crudManager);

        // 4. Start Javalin
        Javalin app = Javalin.create(config -> {
            config.router.apiBuilder(() -> {
                // Public routes
                io.javalin.apibuilder.ApiBuilder.get("/api/v2/metadata", controller::getMetadata);
                
                // Secure routes (handled by JwtInterceptor)
                io.javalin.apibuilder.ApiBuilder.before("/api/v2/{resource}*", new JwtInterceptor());
                
                io.javalin.apibuilder.ApiBuilder.get("/api/v2/{resource}", controller::getAll);
                io.javalin.apibuilder.ApiBuilder.post("/api/v2/{resource}", controller::create);
                io.javalin.apibuilder.ApiBuilder.get("/api/v2/{resource}/{id}", controller::getById);
                io.javalin.apibuilder.ApiBuilder.put("/api/v2/{resource}/{id}", controller::update);
                io.javalin.apibuilder.ApiBuilder.delete("/api/v2/{resource}/{id}", controller::delete);
            });
        }).start(8080);

        log.info("🚀 Server started on http://localhost:8080");

        // Shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutting down...");
            app.stop();
            HibernateUtil.close();
        }));
    }
}
