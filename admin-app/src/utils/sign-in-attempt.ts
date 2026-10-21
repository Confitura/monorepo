import { v4 as uuidv4 } from 'uuid'

// Why the identity provider is being visited. Linking a Login to the signed-in User will add its own intent.
export type SignInIntent = 'sign-in'

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

// Consumes the pending attempt. True only when the provider returned that attempt's state and
// it was started for this intent; anything else is a callback this tab never asked for.
export function finishSignInAttempt(
  intent: SignInIntent,
  returnedState: string | undefined,
): boolean {
  const stored = sessionStorage.getItem(STORAGE_KEY)
  sessionStorage.removeItem(STORAGE_KEY)
  if (stored == null) {
    return false
  }
  const attempt: SignInAttempt = JSON.parse(stored)
  return attempt.intent === intent && attempt.state === returnedState
}
