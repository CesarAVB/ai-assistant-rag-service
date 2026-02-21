package br.com.cesaravb.rag_service.dto.request;

/**
* Representa a requisição do frontend.
* conversationId identifica a sessão — o Angular deve gerar e manter esse ID.
*/
public record ChatRequest(String message, String conversationId) {}