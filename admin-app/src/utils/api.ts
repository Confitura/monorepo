import axios from "axios";

let api = axios.create({
  baseURL: import.meta.env.VITE_API_URL,
});
export default api;
