export function safeInternalRedirect(value: unknown): string | null {
  const candidate = Array.isArray(value) ? value[0] : value
  if (typeof candidate !== 'string' || candidate.length > 2048) return null
  if (!candidate.startsWith('/') || candidate.startsWith('//') || candidate.includes('\\'))
    return null
  return candidate
}
