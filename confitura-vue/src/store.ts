import Vue from 'vue';
import Vuex, { StoreOptions } from 'vuex';
import { CHANGE_HEADER_THEME, RootState, WINDOW_RESIZED } from '@/types';

Vue.use(Vuex);
const storeOptions: StoreOptions<RootState> = {
  state: {
    headerTheme: 'default',
    headerHeight: 73,
    windowWidth: 0,
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
  actions: {},
};
export default new Vuex.Store(storeOptions);

