import {defineStore} from "pinia";
import router from "@/plugins/router.ts";
import {api} from "@/utils/api.ts";

export interface User {
  sub: string;
  jti: string;
  isVolunteer: boolean;
  isAdmin: boolean;
  isNew: boolean;
  isSpeaker: boolean;
}

function extractUser(token: string | null): User | null {
  if (token == null) {
    return null;
  }
  const body = token.split(".")[1];
  return JSON.parse(atob(body));
}

export const useAuthStore = defineStore('auth', {
  state: () => {
    let token = localStorage.getItem('token');
    let user = extractUser(token);
    api.defaults.headers.common['Authorization'] = token;
    return {
      user,
      token
    }
  },
  actions: {
    login(token: string) {
      localStorage.setItem('token', token)
      this.token = token;
      this.user = extractUser(token);
      api.defaults.headers.common['Authorization'] = token;
      router.push("/dashboard")
    },
    updateRegistered(name: string) {
      this.user = {
        ...this.user,
        isNew: false,
        sub: name
      };
      router.push("/dashboard")
    },
    logout() {
      localStorage.removeItem('token')
      this.token = null;
      this.user = null;
      api.defaults.headers.common['Authorization'] = null;
      router.push("/login")
    }
  }
})




