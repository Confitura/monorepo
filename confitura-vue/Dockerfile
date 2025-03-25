FROM node:10 as build

WORKDIR /opt/app

COPY package.json yarn.lock ./
RUN yarn install

COPY . ./
RUN npm run build

# Use Nginx to serve the built files
FROM nginx:stable-alpine
COPY --from=build /opt/app/dist /usr/share/nginx/html

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
