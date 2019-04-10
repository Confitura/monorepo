import { Module } from 'vuex';
import axios from 'axios';
import { RootState, UserProfile, Presentation, EmbeddedPresentations } from '@/types';

export const LOAD_CURRENT_PROFILE = 'LOAD_CURRENT_PROFILE';
export const LOAD_CURRENT_PROFILE_PRESENTATIONS = 'LOAD_CURRENT_PROFILE_PRESENTATIONS';
const UPDATE_CURRENT_PROFILE = 'UPDATE_CURRENT_PROFILE';
const UPDATE_CURRENT_PROFILE_PRESENTATIONS = 'UPDATE_CURRENT_PROFILE_PRESENTATIONS';
export const userModule: Module<StoreUserProfile, RootState> = {
  state: {
    currentProfile: null,
    currentProfilePresentations: [],
  },
  mutations: {
    [UPDATE_CURRENT_PROFILE](store, payload: { profile: any }) {
      store.currentProfile = payload.profile;
    },
    [UPDATE_CURRENT_PROFILE_PRESENTATIONS](store, payload: { presentations: any }) {
      store.currentProfilePresentations = payload.presentations;
    },
  },
  actions: {
    [LOAD_CURRENT_PROFILE]({ commit, rootState, rootGetters }) {
      if (rootState.authentication) {
        return axios.get('/api/users/' + rootGetters.user.jti)
          .then((data) => commit(UPDATE_CURRENT_PROFILE, { profile: data.data }));
      }
    },
    [LOAD_CURRENT_PROFILE_PRESENTATIONS]({ commit, rootState, rootGetters }) {
      if (rootState.authentication) {

        return axios.get<EmbeddedPresentations>(`/api/users/${rootGetters.user.jti}/presentations`, {
          params: { projection: 'inlineSpeaker' },
        })
          .then((data) => commit(UPDATE_CURRENT_PROFILE_PRESENTATIONS, {
            presentations: data.data._embedded.presentations,
          }));
      }
    },
  },
};

interface StoreUserProfile {
  currentProfile: UserProfile | null;
  currentProfilePresentations: Presentation[];
}
