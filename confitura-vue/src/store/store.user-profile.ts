import { Module } from 'vuex';
import axios from 'axios';
import { RootState, UserProfile } from '@/types';

export const LOAD_CURRENT_PROFILE = 'LOAD_CURRENT_PROFILE';
const UPDATE_CURRENT_PROFILE = 'UPDATE_CURRENT_PROFILE';
export const userModule: Module<StoreUserProfile, RootState> = {
  state: {
    currentProfile: null,
  },
  mutations: {
    [UPDATE_CURRENT_PROFILE](store, payload: { profile: any }) {
      store.currentProfile = payload.profile;
    },
  },
  actions: {
    [LOAD_CURRENT_PROFILE]({ commit, rootState, rootGetters }) {
      if (rootState.authentication) {
        return axios.get('/api/users/' + rootGetters.user.jti)
          .then((data) => commit(UPDATE_CURRENT_PROFILE, { profile: data.data }));
      }
    },
  },
};

interface StoreUserProfile {
  currentProfile: UserProfile | null;
}
