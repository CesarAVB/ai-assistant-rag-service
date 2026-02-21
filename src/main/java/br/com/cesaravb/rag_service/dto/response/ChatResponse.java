package br.com.cesaravb.rag_service.dto.response;

/**
 * Representa a resposta enviada ao frontend.
 * conversationId é retornado para o Angular manter o vínculo com a sessão.
 * response é o texto gerado pelo assistente de vendas.
 */
public record ChatResponse(String conversationId, String response) {}