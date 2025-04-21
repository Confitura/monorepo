import axios from "axios";
import {
  AdminPresentationControllerApi,
  DashboardControllerApi,
  PageControllerApi,
  PresentationControllerApi,
  TokenControllerApi,
  UserAdminControllerApi,
  UserControllerApi
} from "./api-axios-client";

export let api = axios.create({
  baseURL: import.meta.env.VITE_API_URL,
});

export let usersApi = new UserControllerApi(undefined, import.meta.env.VITE_API_URL, api)
export let pagesApi = new PageControllerApi(undefined, import.meta.env.VITE_API_URL, api)
export let presentationApi = new PresentationControllerApi(undefined, import.meta.env.VITE_API_URL, api)
export let adminPresentationApi = new AdminPresentationControllerApi(undefined, import.meta.env.VITE_API_URL, api)
export let adminUsersApi = new UserAdminControllerApi(undefined, import.meta.env.VITE_API_URL, api)
export let dashboardApi = new DashboardControllerApi(undefined, import.meta.env.VITE_API_URL, api)
export let tokenAPi = new TokenControllerApi(undefined, import.meta.env.VITE_API_URL, api)
