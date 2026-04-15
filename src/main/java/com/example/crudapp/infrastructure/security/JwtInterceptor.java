package com.example.crudapp.infrastructure.security;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

public class JwtInterceptor implements Handler {
    private static final String SECRET = "your-very-secure-and-long-secret-key-for-jwt-validation";
    private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    @Override
    public void handle(Context ctx) throws Exception {
        String authHeader = ctx.header("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            ctx.status(401).result("Missing or invalid token");
            ctx.skipRemainingHandlers();
            return;
        }

        String token = authHeader.substring(7);
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(KEY)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            
            ctx.attribute("user", claims.getSubject());
        } catch (Exception e) {
            ctx.status(401).result("Invalid token: " + e.getMessage());
            ctx.skipRemainingHandlers();
        }
    }
}
