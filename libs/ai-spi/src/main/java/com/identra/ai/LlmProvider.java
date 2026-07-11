package com.identra.ai;

/** Pluggable LLM provider (Azure OpenAI, OpenAI, Claude, Gemini, Llama, Mistral). */
public interface LlmProvider {

    String id();

    boolean supports(String model);

    LlmResponse complete(LlmRequest request);
}
