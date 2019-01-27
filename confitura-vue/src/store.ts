import Vue from 'vue';
import Vuex, { StoreOptions } from 'vuex';
import { CHANGE_HEADER_THEME, RootState } from '@/types';

Vue.use(Vuex);
const storeOptions: StoreOptions<RootState> = {
  state: {
    headerTheme: 'default',
  },
  mutations: {
    [CHANGE_HEADER_THEME](store, theme: { color: string }) {
      store.headerTheme = theme.color;
    },
  },
  actions: {},
};
export default new Vuex.Store(storeOptions);

