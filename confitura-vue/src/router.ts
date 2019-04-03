import Vue from 'vue';
import Router from 'vue-router';
import Home from './views/Home.vue';
import Faq from './views/Faq.vue';
import VueScrollTo from 'vue-scrollto';
import Partners from '@/views/Partners.vue';
import PartnerPage from '@/views/PartnerPage.vue';
import Login from '@/views/Login.vue';
import RegisterPage from '@/views/profile/RegisterPage.vue';
import ProfilePage from '@/views/profile/ProfilePage.vue';
import store from './store';

Vue.use(Router);
Vue.use(VueScrollTo, {
  container: '.home__container',
});

const router = new Router({
  mode: 'history',
  base: process.env.BASE_URL,
  routes: [
    {
      path: '/',
      name: 'home',
      component: Home,
      beforeEnter: (to, from, next) => {
        if (to.hash) {
          next();
        } else {
          next('/#home');
        }
      },
    },
    {
      path: '/faq',
      name: 'faq',
      component: Faq,
    },
    {
      path: '/partners',
      name: 'partners',
      component: Partners,
    },
    {
      path: '/partners/:id',
      name: 'partner',
      component: PartnerPage,
    },
    {
      path: '/login/:service?',
      name: 'login',
      component: Login,
    },
    {
      path: '/register',
      name: 'register',
      component: RegisterPage,
    },
    {
      path: '/profile',
      name: 'profile',
      component: ProfilePage,
    },
  ],
  scrollBehavior(to, from, savedPosition) {
    if (to.hash) {
      return VueScrollTo.scrollTo(to.hash, 500);
    }
  },

});
router.beforeEach((to, from, next) => {
  const isSessionActive = store.getters.isLogin;
  const accessingProtectedResource = ['/register', '/profile'].includes(to.path);
  if (accessingProtectedResource && !isSessionActive) {
    next('login');
  } else {
    next();
  }
});
export default router;
