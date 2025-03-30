import {createRouter, createWebHistory} from 'vue-router/auto'
import {routes} from 'vue-router/auto-routes'
import {setupLayouts} from 'virtual:meta-layouts'

let publicPaths = ['/privacy-policy']

let router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: setupLayouts(routes),
})
router.beforeEach((to, from, next) => {

  const isPublicPath = (path: string) => publicPaths.includes(path);
  const isLoginPath = (path: string) => path.startsWith('/login');
  const isRegistrationPath = (path: string) => path === '/register';

  if (isPublicPath(to.path)) {
    return next();
  }

  const currentUser = useAuthStore().user;

  if (currentUser === null) {
    return isLoginPath(to.name) ? next() : next('/login');
  }
  if (currentUser.isNew) {
    return isRegistrationPath(to.name) ? next() : next('/register');
  }
  return next();
});


export default router;
