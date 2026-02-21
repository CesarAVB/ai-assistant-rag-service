package br.com.cesaravb.rag_service.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// ========================================
// AuthFilter - Intercepta todas as requisições
// e valida o token no header Authorization.
// Rotas públicas (como /api/auth/login) são
// liberadas sem necessidade de token.
// ========================================
@Component
public class AuthFilter extends OncePerRequestFilter {

    @Value("${app.auth.token}")
    private String validToken;

    // ========================================
    // doFilterInternal - Executado a cada requisição.
    // Libera rotas públicas, bloqueia as demais
    // sem token válido retornando 401.
    // ========================================
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // Libera o endpoint de login e o preflight do CORS (OPTIONS)
        if (path.startsWith("/api/auth") || "OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        // Lê o header Authorization: Bearer <token>
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            // Valida se o token confere com o configurado no application.properties
            if (validToken.equals(token)) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        // Token ausente ou inválido — bloqueia a requisição
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"Token inválido ou ausente.\"}");
    }
}