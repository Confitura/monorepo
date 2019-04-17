import { Module } from 'vuex';
import { RootState, UserProfile, EmbeddedUserProfiles } from '@/types';
import axios from 'axios';

export const LOAD_USERS = 'LOAD_USERS';
const SET_USERS = 'SET_USERS';

export const adminModule: Module<AdminState, RootState> = {
  state: {
    users: [],
  },
  getters: {
    userCount: ({ users }) => users.length,
  },
  mutations: {
    [SET_USERS](store, payload: { users: UserProfile[] }) {
      store.users = payload.users;
    },
  },
  actions: {
    [LOAD_USERS]({ commit, rootState, rootGetters }) {
      return axios.get<EmbeddedUserProfiles>('/api/users')
        .then((it) => {
          commit(SET_USERS, { users: it.data._embedded.users });
        });
    },
  },
};

export interface AdminState {
  users: UserProfile[];
}
