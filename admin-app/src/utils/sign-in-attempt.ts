import { v4 as uuidv4 } from 'uuid'

// Why the identity provider is being visited: to sign in, or to add a Login to the signed-in User.
export type SignInIntent = 'sign-in' | 'add-login'

// The identity providers a User can sign in with: the backend's name for each, and the one people know.
export const SIGN_IN_PROVIDERS = [
  { id: 'github', label: 'GitHub' },
  { id: 'google', label: 'Google' },
  { id: 'facebook', label: 'Facebook' },
  { id: 'twitter', label: 'X' },
]

export function providerLabel(provider: string | undefined): string {
  return SIGN_IN_PROVIDERS.find((p) => p.id === provider)?.label ?? provider ?? ''
}

interface SignInAttempt {
  state: string
  intent: SignInIntent
}

// sessionStorage keeps the attempt in this tab only, across the round trip to the provider.
const STORAGE_KEY = 'sign-in-attempt'

// Returns the fresh random state to send to the provider.
export function startSignInAttempt(intent: SignInIntent): string {
  const attempt: SignInAttempt = { state: uuidv4(), intent }
  sessionStorage.setItem(STORAGE_KEY, JSON.stringify(attempt))
  return attempt.state
}

// Sends the browser to the provider's sign-in. The state is created per call, not at render, so every
// attempt gets its own.
export function goToSignIn(provider: string, intent: SignInIntent) {
  const url = new URL(`${import.meta.env.VITE_API_URL}/login/${provider}`)
  url.searchParams.set('redirect_uri', `${import.meta.env.VITE_SELF_URL}/login/${provider}`)
  url.searchParams.set('state', startSignInAttempt(intent))
  window.location.assign(url.toString())
}

// Consumes the pending attempt and returns what it was started for, but only when the provider
// returned that attempt's state; null means a callback this tab never asked for.
export function finishSignInAttempt(returnedState: string | undefined): SignInIntent | null {
  const stored = sessionStorage.getItem(STORAGE_KEY)
  sessionStorage.removeItem(STORAGE_KEY)
  if (stored == null) {
    return null
  }
  const attempt: SignInAttempt = JSON.parse(stored)
  return attempt.state === returnedState ? attempt.intent : null
}
