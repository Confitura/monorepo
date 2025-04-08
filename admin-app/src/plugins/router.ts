import {createRouter, createWebHistory} from 'vue-router/auto'
import {routes} from 'vue-router/auto-routes'
import {setupLayouts} from 'virtual:meta-layouts'
import type {RouteMeta} from "vue-router";

let publicPaths = ['/privacy-policy']

let router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: setupLayouts(routes),
})
router.beforeEach((to, from, next) => {

  let pathMeta: RouteMeta = to.meta
  if (!pathMeta.requiresAuth) {
    return next();
  }

  const currentUser = useAuthStore().user;

  if (currentUser === null) {
    return to.name.startsWith('/login') ? next() : next('/login');
  }
  if (currentUser.isNew) {
    return to.name === '/register' ? next() : next('/register');
  }
  return next();
});


export default router;
