package br.com.cesaravb.rag_service.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.cesaravb.rag_service.dto.request.AuthRequest;
import br.com.cesaravb.rag_service.dto.response.AuthResponse;

// ========================================
// AuthController - Endpoint de login.
// Recebe a senha enviada pelo Angular,
// valida contra o valor configurado no
// application.properties e retorna o token
// estático caso a senha esteja correta.
// ========================================
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Value("${app.auth.password}")
    private String validPassword;

    @Value("${app.auth.token}")
    private String validToken;

    // ========================================
    // login - Valida a senha enviada pelo Angular. Retorna 200 + token se correta ou 401 se a senha estiver errada.
    // ========================================
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        if (validPassword.equals(request.password())) {
            return ResponseEntity.ok(new AuthResponse(validToken));
        }
        return ResponseEntity.status(401).body(new AuthResponse(null));
    }
}