import { Module } from "vuex";
import axios from "axios";
import {
  RootState,
  UserProfile,
  Presentation,
  EmbeddedPresentations,
} from "@/types";

export const LOAD_PROFILE_BY_ID = "LOAD_PROFILE_BY_ID";
export const LOAD_PROFILE_PARTICIPATION_BY_ID =
  "LOAD_PROFILE_PARTICIPATION_BY_ID";
export const LOAD_PROFILE_PRESENTATIONS_BY_ID =
  "LOAD_PROFILE_PRESENTATIONS_BY_ID";
const UPDATE_CURRENT_PROFILE = "UPDATE_CURRENT_PROFILE";
const UPDATE_CURRENT_PROFILE_PARTICIPATION =
  "UPDATE_CURRENT_PROFILE_PARTICIPATION";
const UPDATE_CURRENT_PROFILE_PRESENTATIONS =
  "UPDATE_CURRENT_PROFILE_PRESENTATIONS";
export const userModule: Module<StoreUserProfile, RootState> = {
  state: {
    currentProfile: null,
    currentProfilePresentations: [],
    participation: null,
  },
  mutations: {
    [UPDATE_CURRENT_PROFILE](store, payload: { profile: any }) {
      store.currentProfile = payload.profile;
    },
    [UPDATE_CURRENT_PROFILE_PRESENTATIONS](
      store,
      payload: { presentations: any }
    ) {
      store.currentProfilePresentations = payload.presentations;
    },
    [UPDATE_CURRENT_PROFILE_PARTICIPATION](
      store,
      payload: { participation: any }
    ) {
      store.participation = payload.participation;
    },
  },
  actions: {
    [LOAD_PROFILE_BY_ID](
      { commit, rootState, rootGetters },
      params: { id: string }
    ) {
      if (rootState.authentication) {
        return axios
          .get("/api/users/" + params.id)
          .then((data) =>
            commit(UPDATE_CURRENT_PROFILE, { profile: data.data })
          );
      }
    },
    [LOAD_PROFILE_PARTICIPATION_BY_ID](
      { commit, rootState, rootGetters },
      params: { id: string }
    ) {
      if (rootState.authentication) {
        return axios
          .get("/api/users/" + params.id + "/participationData")
          .then((data) =>
            commit(UPDATE_CURRENT_PROFILE_PARTICIPATION, {
              participation: data.data,
            })
          );
      }
    },
    [LOAD_PROFILE_PRESENTATIONS_BY_ID](
      { commit, rootState, rootGetters },
      params: { id: string }
    ) {
      if (rootState.authentication) {
        return axios
          .get<EmbeddedPresentations>(`/api/users/${params.id}/presentations`, {
            params: { projection: "inlineSpeaker" },
          })
          .then((data) =>
            commit(UPDATE_CURRENT_PROFILE_PRESENTATIONS, {
              presentations: data.data._embedded.presentations,
            })
          );
      }
    },
  },
};

interface StoreUserProfile {
  currentProfile: UserProfile | null;
  currentProfilePresentations: Presentation[];
  participation: any;
}
