package br.com.cesaravb.rag_service.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.cesaravb.rag_service.dto.request.ChatRequest;
import br.com.cesaravb.rag_service.dto.response.ChatResponse;
import br.com.cesaravb.rag_service.service.ChatService;

/**
 * ChatController expõe o endpoint REST do assistente de vendas.
 * Recebe as mensagens do frontend (Angular) e retorna as respostas da IA.
 */
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    // ============================================================================================================
    // O endpoint POST /api/chat recebe um ChatRequest contendo a mensagem do usuário e o ID da conversa.
    // ============================================================================================================
    @PostMapping
    public ChatResponse chat(@RequestBody ChatRequest request) {
        String response = chatService.chat(request.message(), request.conversationId());
        return new ChatResponse(request.conversationId(), response);
    }
}