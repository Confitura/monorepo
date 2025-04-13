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
    return to.name === '/profile-form' ? next() : next('/profile-form');
  }
  if (pathMeta?.requiresAdmin && !currentUser.isAdmin) {
    Notify.warning(`You do not have the required permissions to access this page`)
    console.log("You do not have the required permissions to access this page", to)
    return null;
  }
  return next();
});


export default router;
