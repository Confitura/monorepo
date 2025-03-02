import {createRouter, createWebHistory} from 'vue-router/auto'
import {routes} from 'vue-router/auto-routes'
import {setupLayouts} from 'virtual:meta-layouts'

let router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: setupLayouts(routes),
})
router.beforeEach((to, from, next) => {
  if (to.meta.requiresAuth) {
    let user = useAuthStore().user;
    if (user == null) {
      next('/login');
    } else if (user.isNew) {
      next('/register');
    } else {
      next();
    }
  } else {
    // Non-protected route, allow access
    next();
  }
});

export default router;
