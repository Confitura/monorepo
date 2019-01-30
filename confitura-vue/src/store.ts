import Vue from 'vue';
import Vuex, { StoreOptions } from 'vuex';
import { CHANGE_HEADER_THEME, RootState, WINDOW_RESIZED } from '@/types';

Vue.use(Vuex);
const storeOptions: StoreOptions<RootState> = {
  state: {
    headerTheme: 'default',
    headerHeight: 73,
  },
  mutations: {
    [CHANGE_HEADER_THEME](store, theme: { color: string }) {
      store.headerTheme = theme.color;
    },

    [WINDOW_RESIZED](store, size: { width: number }) {
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

