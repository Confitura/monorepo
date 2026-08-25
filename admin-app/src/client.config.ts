import type { CreateClientConfig } from './client/client.gen'

// Runtime configuration merged into the generated hey-api client. Auth headers are attached
// via an interceptor in src/utils/api.ts, which imports and configures this same client.
export const createClientConfig: CreateClientConfig = (config) => ({
  ...config,
  baseURL: import.meta.env.VITE_API_URL,
})
