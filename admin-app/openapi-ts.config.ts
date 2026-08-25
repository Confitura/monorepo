import { defineConfig } from '@hey-api/openapi-ts'

export default defineConfig({
  input: '../openapi.json',
  output: {
    path: 'src/client',
  },
  plugins: [
    { name: '@hey-api/client-axios', runtimeConfigPath: './src/client.config.ts' },
    '@hey-api/sdk',
    '@hey-api/typescript',
  ],
})
