# AI Safety Guidelines

- No production secrets in prompts
- PII minimization / tokenization before LLM calls
- Output allowlists (file types, APIs)
- Human approval below governance score thresholds
- Prompt-injection defenses on marketplace RAG
- Model card + eval reports per AI release
- Provider/model kill switch via AI Gateway
- Multi-LLM routing: Azure OpenAI, OpenAI, Claude, Gemini, Llama, Mistral
