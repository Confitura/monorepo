import { Module } from "vuex";
import { Presentation, PresentationRate, RootState } from "@/types";
import axios from "axios";
import { getToken } from "@/utils";

export const SET_PRESENTATION_UNDER_RATE = "SET_PRESENTATION_UNDER_RATE";
export const RATE = "RATE";

const RATING_TOKEN = "RATING_TOKEN";

export const presentationsModule: Module<PresentationsState, RootState> = {
  state: {
    underRating: null,
    rates: JSON.parse(localStorage.getItem("rate") || "[]")
  },
  getters: {
    isRated(state) {
      return (id: string) => state.rates.includes(id);
    }
  },
  mutations: {
    [SET_PRESENTATION_UNDER_RATE](
      store,
      payload: { presentationRate: PresentationRate }
    ) {
      store.underRating = payload.presentationRate;
    },
    [RATE](store, payload: { presentation: Presentation; id: string }) {
      const { presentation } = payload;
      if (presentation.id) {
        store.rates = [presentation.id, ...store.rates];
        localStorage.setItem(`rate`, JSON.stringify(store.rates));
      }
    }
  },
  actions: {
    [RATE]({ commit }, { presentation, rate, comment }: PresentationRate) {
      if (presentation) {
        let reviewerToken = getToken(RATING_TOKEN);
        return axios
          .post<{ id: string }>(
            `/api/presentations/${presentation.id}/ratings`,
            { value: rate - 1, comment, reviewerToken }
          )
          .then(response => {
            commit(RATE, { presentation, id: response.data.id });
          });
      }
    }
  }
};

export interface PresentationsState {
  underRating: PresentationRate | null;
  rates: string[];
}
