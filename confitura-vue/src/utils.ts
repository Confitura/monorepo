import * as jsSHA from "jssha";

export function getToken(tokenName: string) {
  const localStorageToken = localStorage.getItem(tokenName);
  if (localStorageToken) {
    return localStorageToken;
  } else {
    const token = generateToken();
    localStorage.setItem(tokenName, token);
    return token;
  }
}

function generateToken() {
  const jssha = jsSHA as any;
  const sha = new jssha("SHA-256", "TEXT");
  sha.update(`${new Date().getMilliseconds()}${Math.random()}`);
  return sha.getHash("HEX");
}