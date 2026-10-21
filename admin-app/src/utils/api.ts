import { client } from '@/client/client.gen'

// Attach the JWT as a Bearer token on every request. The backend tolerates both a raw token and
// a `Bearer `-prefixed one (JwtAuthenticationFilter strips the prefix); we send the standard form
// matching the OpenAPI `bearerAuth` scheme. The token lives in localStorage, managed by the auth store.
client.instance.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.set('Authorization', `Bearer ${token}`)
  }
  return config
})

// A 401 means the backend no longer accepts the token (it expired, or its User was deleted or merged
// into another), so the user is signed out: the auth store clears the token and goes to the login page.
client.instance.interceptors.response.use(undefined, (error) => {
  if (error.response?.status === 401) {
    useAuthStore().logout()
  }
  return Promise.reject(error)
})

// Single import surface for the generated client: SDK functions, types, and the client instance.
export * from '@/client'
