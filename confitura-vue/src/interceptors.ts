import axios, { AxiosError } from "axios";
import store from "@/store";
import { LOGOUT } from "@/types";

axios.interceptors.request.use(
  config => {
    if (
      store!.state!.authentication!.token &&
      store!.state!.authentication!.token !== "null"
    ) {
      config.headers.Authorization = `Bearer ${
        store!.state!.authentication!.token
      }`;
    }
    return config;
  },
  error => {
    return Promise.reject(error);
  }
);
