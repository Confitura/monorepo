import { defineConfig } from '@hey-api/openapi-ts'

export default defineConfig({
  input: '../openapi.json',
  output: {
    path: 'src/client',
  },
  plugins: [
    // throwOnError: a request the backend refuses rejects, so it reaches the caller's catch rather than its
    // success path. Set here, not in the runtime config, so the generated types know it too.
    { name: '@hey-api/client-axios', runtimeConfigPath: './src/client.config.ts', throwOnError: true },
    '@hey-api/sdk',
    '@hey-api/typescript',
  ],
})
