import { Module } from "vuex";
import {
  EmbeddedPresentations,
  EmbeddedUserProfiles,
  Presentation,
  RootState,
  UserProfile,
  Voucher
} from "@/types";
import axios from "axios";

export const LOAD_USERS = "LOAD_USERS";
export const LOAD_SPEAKERS = "LOAD_SPEAKERS";
export const LOAD_SPEAKER = "LOAD_SPEAKER";
export const LOAD_ALL_PRESENTATIONS = "LOAD_ALL_PRESENTATIONS";
export const LOAD_ACCEPTED_PRESENTATIONS = "LOAD_ACCEPTED_PRESENTATIONS";
export const LOAD_VOUCHERS = "LOAD_VOUCHERS";
export const LOAD_INFO = "LOAD_INFO";
const SET_USERS = "SET_USERS";
const SET_SPEAKERS = "SET_SPEAKERS";
const SET_SPEAKER = "SET_SPEAKER";
const SET_PRESENTATIONS = "SET_PRESENTATIONS ";
const SET_VOUCHERS = "SET_VOUCHERS";
const SET_INFO = "SET_INFO";

export const adminModule: Module<AdminState, RootState> = {
  state: {
    users: [],
    speakers: [],
    speaker: null,
    presentations: [],
    vouchers: [],
    info: {}
  },
  getters: {
    userCount: ({ users }) => users.length,
    presentationCount: ({ presentations }) => presentations.length,
    vouchersCount: ({ vouchers }) => vouchers.length,
    info: ({ info }) => info
  },
  mutations: {
    [SET_USERS](store, payload: { users: UserProfile[] }) {
      store.users = payload.users.sort((a, b) =>
        (a.name || "") > (b.name || "") ? 1 : -1
      );
    },
    [SET_VOUCHERS](store, payload: { vouchers: Voucher[] }) {
      store.vouchers = payload.vouchers;
    },
    [SET_SPEAKERS](store, payload: { speakers: UserProfile[] }) {
      store.speakers = payload.speakers;
    },
    [SET_SPEAKER](store, payload: { speaker: UserProfile }) {
      store.speaker = payload.speaker;
    },
    [SET_PRESENTATIONS](store, payload: { presentations: Presentation[] }) {
      store.presentations = payload.presentations.sort((a, b) =>
        a.title > b.title ? 1 : -1
      );
    },
    [SET_INFO](store, payload: { info: Info }) {
      store.info = payload.info;
    }
  },
  actions: {
    [LOAD_USERS]({ commit }) {
      return axios.get<EmbeddedUserProfiles>("/api/users").then(it => {
        commit(SET_USERS, {
          users: it.data._embedded.publicUsers || it.data._embedded.users
        });
      });
    },
    [LOAD_VOUCHERS]({ commit }) {
      return axios.get<Voucher[]>("/api/vouchers").then(it => {
        commit(SET_VOUCHERS, { vouchers: it.data });
      });
    },
    [LOAD_SPEAKERS]({ commit }) {
      return axios
        .get<EmbeddedUserProfiles>("/api/users/search/speakers")
        .then(it => {
          commit(SET_SPEAKERS, { speakers: it.data._embedded.publicUsers });
        });
    },
    [LOAD_SPEAKER]({ commit }, { id }) {
      return axios.get<UserProfile>(`/api/users/${id}/public`).then(it => {
        commit(SET_SPEAKER, { speaker: it.data });
      });
    },
    [LOAD_ALL_PRESENTATIONS]({ commit }) {
      return axios
        .get<EmbeddedPresentations>("/api/presentations", {
          params: { projection: "inlineSpeaker" }
        })
        .then(it => {
          commit(SET_PRESENTATIONS, {
            presentations: it.data._embedded.presentations
          });
        });
    },
    [LOAD_ACCEPTED_PRESENTATIONS]({ commit }) {
      return axios
        .get<EmbeddedPresentations>("/api/presentations/search/accepted", {
          params: { projection: "inlineSpeaker" }
        })
        .then(it => it.data._embedded.presentations)
        .then(presentations => {
          commit(SET_PRESENTATIONS, { presentations });
        });
    },
    [LOAD_INFO]({ commit }) {
      return axios
        .get<Info>("/api/api/actuator/info")
        .then(it => it.data)
        .then(info => {
          commit(SET_INFO, { info });
        });
    }
  }
};

export interface AdminState {
  users: UserProfile[];
  speakers: UserProfile[];
  presentations: Presentation[];
  speaker: UserProfile | null;
  vouchers: Voucher[];
  info: Info;
}

interface Info {
  c4p?: {
    start: number;
    end: number;
    enabled: boolean;
  };
}
