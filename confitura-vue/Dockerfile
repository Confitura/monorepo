FROM node:10 as build

WORKDIR /opt/app

COPY . ./
RUN npm install
RUN npm run build --prod --modern

# Use Nginx to serve the built files
FROM nginx:stable-alpine
COPY --from=build /opt/app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf


EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
