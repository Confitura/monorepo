import axios from "axios";
import {
  PageControllerApi,
  PresentationControllerApi,
  UserControllerApi
} from "./api-axios-client";

export let api = axios.create({
  baseURL: import.meta.env.VITE_API_URL,
});

export let usersApi = new UserControllerApi(undefined, import.meta.env.VITE_API_URL, api)
export let pagesApi = new PageControllerApi(undefined, import.meta.env.VITE_API_URL, api)
export let presentationApi = new PresentationControllerApi(undefined, import.meta.env.VITE_API_URL, api)
