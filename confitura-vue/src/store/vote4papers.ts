import { Module } from "vuex";
import { RootState, Vote } from "@/types";
import axios from "axios";
import { getToken } from "@/utils";

export const LOAD_VOTES = "LOAD_VOTES";
export const SAVE_VOTE = "SAVE_VOTE";

const SET_VOTES = "SET_VOTES";
const V4P_TOKEN = "V4P_TOKEN";

export const vote4PapersModule: Module<Vote4PapersState, RootState> = {
  state: {
    votes: []
  },
  getters: {},
  mutations: {
    [SET_VOTES](store, payload: { votes: Vote[] }) {
      store.votes = payload.votes;
    }
  },
  actions: {
    [LOAD_VOTES]({ commit, rootState, rootGetters }) {
      const token = getToken(V4P_TOKEN);

      return axios
        .post<EmbeddedVotes>(`/api/votes/start/${token}`)
        .then(it => commit(SET_VOTES, { votes: it.data._embedded.votes }));
    },
    [SAVE_VOTE]({ commit }, payload: { vote: Vote }) {
      return axios.post<EmbeddedVotes>(`/api/votes/`, payload.vote);
    }
  }
};

export interface Vote4PapersState {
  votes: Vote[];
}

interface EmbeddedVotes {
  _embedded: { votes: Vote[] };
}
