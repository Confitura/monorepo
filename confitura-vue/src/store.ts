import Vue from 'vue';
import Vuex, { StoreOptions } from 'vuex';
import { CHANGE_HEADER_THEME, LOAD_PARTNERS, Partner, RootState, WINDOW_RESIZED } from '@/types';

Vue.use(Vuex);
const storeOptions: StoreOptions<RootState> = {
  state: {
    headerTheme: 'default',
    headerHeight: 73,
    windowWidth: 0,
    date: '2019-06-29T09:00',
    partners: [],
  },
  getters: {
    isSm: (state) => state.windowWidth >= 576,
    isMd: (state) => state.windowWidth >= 768,
    isLg: (state) => state.windowWidth >= 992,
    isXl: (state) => state.windowWidth >= 1200,
    platinum: (state): Partner[] => state.partners.filter((partner) => partner.type === 'platinum'),
    silver: (state): Partner[] => state.partners.filter((partner) => partner.type === 'silver'),

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
    [LOAD_PARTNERS]({ state }) {
      state.partners = [
        {
          id: '1',
          description: '',
          name: 'SoftwarePlant',
          www: 'https://softwareplant.com/',
          type: 'platinum',
          logo: require('./assets/partners/softwareplant.svg'),
        }, {
          id: '2',
          description: '',
          name: '7n',
          www: 'https://7n.com/',
          type: 'silver',
          logo: require('./assets/partners/7N.svg'),
        },{
          id: '3',
          description: '',
          name: 'Volvo Group',
          www: 'https://www.volvogroup.pl/kariera',
          type: 'silver',
          logo: require('./assets/partners/volvo.svg'),
        },
      ];

    },
  },
};
export default new Vuex.Store(storeOptions);

