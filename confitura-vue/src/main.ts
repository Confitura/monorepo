import Vue from 'vue';
import App from './App.vue';
import router from './router';
import store from './store';
import 'intersection-observer/intersection-observer';
import '@/interceptors';
import * as VueGoogleMaps from 'vue2-google-maps';

Vue.use(VueGoogleMaps, {
  load: {
    key: 'AIzaSyA6nOtsqyZJRfMwy6kmaK2_MgX51TtmViA',
  },
});


Vue.config.productionTip = false;

new Vue({
  router,
  store,
  render: (h) => h(App),
}).$mount('#app');
