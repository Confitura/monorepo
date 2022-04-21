import Vue from "vue";
import Vuex, { createStore, StoreOptions } from "vuex";
import {
  CHANGE_HEADER_THEME,
  REMOVE_PRESENTATION,
  RootState,
  WINDOW_RESIZED,
} from "@/types";
import { userModule } from "./store.user-profile";
import { authenticationModule } from "./authentication";
import axios from "axios";
import { partnersModule } from "@/store/partners";
import { adminModule } from "@/store/admin";
import { vote4PapersModule } from "@/store/vote4papers";
import { presentationsModule } from "@/store/presentations";

// Vue.use(Vuex);
 export default  createStore<RootState>({
  modules: {
    userProfile: userModule,
    authentication: authenticationModule,
    partners: partnersModule,
    admin: adminModule,
    v4p: vote4PapersModule,
    presentations: presentationsModule,
  },
  state: {
    headerTheme: "default",
    headerHeight: 73,
    windowWidth: 0,
    date: "2020-06-27T09:00",
    partners: { partners: [] },
    authentication: { token: null },
  },
  getters: {
    isSm: (state) => state.windowWidth >= 576,
    isMd: (state) => state.windowWidth >= 768,
    isLg: (state) => state.windowWidth >= 992,
    isXl: (state) => state.windowWidth >= 1200,
  },
  mutations: {
    [CHANGE_HEADER_THEME](store, theme: { color: string }) {
      store.headerTheme = theme.color;
    },

    [WINDOW_RESIZED](store, size: { width: number }) {
      store.windowWidth = size.width;
      if (size.width >= 992) {
        store.headerHeight = 73;
      } else {
        store.headerHeight = 60;
      }
    },
  },
  actions: {
    [REMOVE_PRESENTATION](context, id: string) {
      return axios.delete(`/api/presentations/${id}`);
    },
  },
});
