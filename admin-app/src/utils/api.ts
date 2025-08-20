import axios from "axios";
import {
  AdminPresentationControllerApi,
  AgendaControllerApi,
  DashboardControllerApi, DayControllerApi,
  PageControllerApi,
  PresentationControllerApi, ResourceControllerApi,
  RoomControllerApi,
  TokenControllerApi,
  UserAdminControllerApi,
  UserControllerApi,
  VoteControllerApi,
  AdminTaskControllerApi
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
export let resourcesApi = new ResourceControllerApi(undefined, import.meta.env.VITE_API_URL, api)
export let voteForPapersApi = new VoteControllerApi(undefined, import.meta.env.VITE_API_URL)
export let adminTasksApi = new AdminTaskControllerApi(undefined, import.meta.env.VITE_API_URL, api)
export let daysApi = new DayControllerApi(undefined, import.meta.env.VITE_API_URL)
export let agendaApi = new AgendaControllerApi(undefined, import.meta.env.VITE_API_URL, api)
export let roomsApi = new RoomControllerApi(undefined, import.meta.env.VITE_API_URL, api)
