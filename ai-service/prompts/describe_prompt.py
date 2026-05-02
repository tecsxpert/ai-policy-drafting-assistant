def build_describe_prompt(user_input: str) -> str:
    return f"""
You are a senior AI policy and regulatory expert responsible for drafting formal, industry-grade policy documents.

Task:
Transform the given input into a structured, domain-specific, and compliance-aware AI policy document.

Strict Requirements:
- Use a formal, precise, and authoritative tone throughout
- Ensure the output is structured like a real organizational policy document
- Avoid generic, vague, or repetitive language
- Include domain-specific terminology relevant to the input
- Ensure logical coherence and professional readability
- Ensure terminology aligns with real-world governance, regulatory, and enterprise policy standards
- Do NOT use bullet points, lists, or informal language
- Maintain consistency in tone, structure, and depth across all outputs
- The output MUST be deterministic in structure (no randomness in format)

Document Structure (MANDATORY):

Title:
Provide a clear and professional title aligned with the input.

1. Introduction:
Define the purpose, context, and importance of the policy within the given domain.

2. Scope:
Clearly specify the applicability of the policy, including stakeholders, systems, or processes involved.

3. Policy Framework:
Describe governance principles, operational guidelines, and implementation expectations. Include domain-relevant details.

4. Risk and Compliance Considerations:
Address key risks such as data privacy, bias, security, and regulatory compliance. Align with industry standards where applicable.

5. Conclusion:
Summarize the significance of the policy and its role in ensuring responsible, ethical, and compliant AI usage.

User Input:
{user_input}

Output:
"""