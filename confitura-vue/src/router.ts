import Vue from 'vue';
import Router from 'vue-router';
import Home from './views/Home.vue';
import VueScrollTo from 'vue-scrollto';

Vue.use(Router);
Vue.use(VueScrollTo, {
  container: '.home__container',
});

export default new Router({
  mode: 'history',
  base: process.env.BASE_URL,
  routes: [
    {
      path: '/',
      name: 'home',
      component: Home,
    },
  ],
  scrollBehavior(to, from, savedPosition) {
    if (to.hash) {
      return VueScrollTo.scrollTo(to.hash, 500);
    }
  },
});
