import Vue from "vue";
import Router from "vue-router";
import Home from "./views/Home.vue";
import Faq from "./views/Faq.vue";
import VueScrollTo from "vue-scrollto";
import Partners from "@/views/Partners.vue";
import PartnerPage from "@/views/PartnerPage.vue";
import Login from "@/views/Login.vue";
import RegisterPage from "@/views/profile/RegisterPage.vue";
import store from "./store";
import ProfilePage from "@/views/profile/ProfilePage.vue";
import PresentationForm from "@/views/profile/PresentationForm.vue";
import PrivacyPolicy from "@/views/PrivacyPolicy.vue";
import Admin from "@/views/admin/Admin.vue";
import Status from "@/views/admin/Status";
import Users from "@/views/admin/Users.vue";
import Presentations from "@/views/admin/Presentations.vue";
import Workshops from "@/views/Workshops.vue";
import Vote4Papers from "@/views/Vote4Papers.vue";
import RegistrationInfoPage from "@/views/RegistrationInfoPage.vue";
import ParticipatePage from "@/views/participant/ParticipatePage.vue";
import ParticipantPage from "@/views/participant/ParticipantPage.vue";
import Speakers from "@/views/Speakers.vue";
import Speaker from "@/views/Speaker.vue";
import AcceptedPresentations from "@/views/AcceptedPresentations.vue";
import Vouchers from "@/views/admin/Vouchers.vue";
import About from "@/views/About.vue";
import Agenda from "@/views/Agenda.vue";
import Venue from "@/views/Venue.vue";
import Scanner from "@/views/admin/Scanner.vue";
import Spoina from "@/views/Spoina.vue";

Vue.use(Router);
Vue.use(VueScrollTo, {
  container: ".home__container"
});

const router = new Router({
  mode: "history",
  base: process.env.BASE_URL,
  routes: [
    {
      path: "/",
      name: "home",
      component: Home,
      beforeEnter: (to, from, next) => {
        if (to.hash) {
          next();
        } else {
          next("/#home");
        }
      }
    },
    {
      path: "/faq",
      name: "faq",
      component: Faq
    },
    {
      path: "/partners",
      name: "partners",
      component: Partners
    },
    {
      path: "/partners/:id",
      name: "partner",
      component: PartnerPage
    },
    {
      path: "/login/:service?",
      alias: "/c4p/:service?",
      name: "login",
      component: Login
    },
    {
      path: "/register/:id?",
      name: "register",
      component: RegisterPage
    },
    {
      path: "/profile/:id?",
      name: "profile",
      component: ProfilePage
    },
    {
      path: "/profile/:userId/presentation/:id?",
      name: "presentation",
      component: PresentationForm
    },
    {
      path: "/privacy-policy",
      name: "privacy-policy",
      component: PrivacyPolicy
    },
    {
      path: "/admin",
      name: "admin",
      component: Admin,
      children: [
        { path: "", component: Status },
        { path: "users", component: Users },
        { path: "presentations", component: Presentations },
        { path: "vouchers", component: Vouchers }
      ]
    },
    {
      path: "/workshops",
      name: "workshops",
      component: Workshops
    },
    {
      path: "/v4p",
      name: "v4p",
      component: Vote4Papers
    },
    {
      path: "/tickets",
      name: "tickets",
      component: RegistrationInfoPage
    },
    {
      path: "/participate",
      name: "participate",
      component: ParticipatePage
    },
    {
      path: "/participant/:id",
      name: "participant",
      component: ParticipantPage
    },
    {
      path: "/speakers",
      name: "speakers",
      component: Speakers
    },
    {
      path: "/speakers/:id",
      name: "speaker",
      component: Speaker
    },
    {
      path: "/presentations",
      name: "presentations",
      component: AcceptedPresentations
    },
    {
      path: "/scanner/:id?",
      name: "scanner",
      component: Scanner
    },
    {
      path: "/about",
      name: "about",
      component: About
    },
    {
      path: "/schedule",
      name: "schedule",
      component: Agenda
    },
    {
      path: "/venue",
      name: "venue",
      component: Venue
    },
    {
      path: "/spoina",
      name: "spoina",
      component: Spoina
    }
  ],
  scrollBehavior(to, from, savedPosition) {
    if (to.hash) {
      return VueScrollTo.scrollTo(to.hash, 500);
    } else if (savedPosition) {
      return savedPosition;
    } else {
      return { x: 0, y: 0 };
    }
  }
});
router.beforeEach((to, from, next) => {
  const isSessionActive = store.getters.isLogin;
  const accessingProtectedResource = [
    "/register",
    "/profile",
    "/presentation"
  ].includes(to.path);
  if (accessingProtectedResource && !isSessionActive) {
    next("login");
  } else {
    next();
  }
});
export default router;
