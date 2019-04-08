import axios, { AxiosError } from 'axios';
import store from '@/store';
import { LOGOUT } from '@/types';

axios.interceptors.request.use((config) => {
  if (store!.state!.authentication!.token && store!.state!.authentication!.token !== 'null') {
    config.headers.Authorization = `Bearer ${store!.state!.authentication!.token}`;
  }
  return config;
}, (error) => {
  return Promise.reject(error);
});
axios.interceptors.response.use((config) => {
  return config;
}, (error: AxiosError) => {
  if (error!.response!.status === 401) {
    store.dispatch(LOGOUT);
  }
  return Promise.reject(error);
});
