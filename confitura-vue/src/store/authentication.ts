import { Module } from 'vuex';
import { LOGIN, LOGOUT, RootState, TOKEN, User } from '@/types';
import axios from 'axios';
import router from '../router';

export const authenticationModule: Module<AuthenticationState, RootState> = {
  state: {
    token: localStorage.getItem(TOKEN),
  },

  getters: {
    user: ({ token }): User | null => {
      if (token) {
        try {
          const body = token.split('.')[1];
          return JSON.parse(atob(body)) as User;
        } catch (error) {
          return null;
        }
      } else {
        return null;
      }
    },
    isLogin: (state, getters) => {
      return getters.user !== null;
    },
    isAdmin: (state, getters) => {
      const user: User | null = getters.user;
      return user !== null && user.isAdmin;
    },
    isVolunteer: (state, getters) => {
      const user: User | null = getters.user;
      return user !== null && user.isVolunteer;
    },
  },
  mutations: {
    [TOKEN](store, payload: { token: string }) {
      store.token = payload.token;
      if (payload.token == null) {
        localStorage.removeItem(TOKEN);
      } else {
        localStorage.setItem(TOKEN, payload.token);
      }
    },
  },
  actions: {
    [LOGIN]({ commit, rootGetters }, { service, params }) {
      axios.get(`/api/login/${service}/callback`, { params })
        .then(({ data }) => {
          commit(TOKEN, { token: data });
        })
        .then(() => {
            const { isNew } = rootGetters.user;
            if (isNew) {
              router.push('/register');
            } else {
              router.push('/profile');
            }
          },
        );
    },

    [LOGOUT]({ commit }) {
      commit(TOKEN, { token: null });
      router.push('/');
    },
  },
};

export interface AuthenticationState {
  token: string | null;
}
