import {createRouter, createWebHistory} from 'vue-router/auto'
import {routes} from 'vue-router/auto-routes'
import {setupLayouts} from 'virtual:meta-layouts'

let router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: setupLayouts(routes),
})
router.beforeEach((to, from, next) => {
  let user = useAuthStore().user;
  if (user == null) {
    if (to.name.startsWith('/login')) {
      next();
    } else {
      next('/login');
    }
  } else if (user.isNew) {
    if (to.name === '/register') {
      next();
    } else {
      next('/register');
    }
  } else {
    next();
  }
})

export default router;
