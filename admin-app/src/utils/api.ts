import axios from "axios";

let api = axios.create({
  baseURL: 'http://localhost:8080/'
});
export default api;
