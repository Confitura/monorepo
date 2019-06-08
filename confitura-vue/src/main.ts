import Vue from 'vue';
import App from './App.vue';
import router from './router';
import store from './store';
import 'intersection-observer/intersection-observer';
import '@/interceptors';
import * as VueGoogleMaps from 'vue2-google-maps';
import VueHotkey from 'v-hotkey';

Vue.use(VueHotkey);
Vue.use(VueGoogleMaps, {
  load: {
    key: 'AIzaSyA6nOtsqyZJRfMwy6kmaK2_MgX51TtmViA',
  },
});


Vue.config.productionTip = false;
Vue.filter('firstName', (value: string): string => {
  const name = value || '';
  const idx = name.indexOf(' ');
  return name.substring(0, idx);
});

Vue.filter('lastName', (value: string): string => {
  const name = value || '';
  const idx = name.indexOf(' ');
  return name.substring(idx);
});

new Vue({
  router,
  store,
  render: (h) => h(App),
}).$mount('#app');
