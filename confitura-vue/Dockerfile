FROM node:10

WORKDIR /opt/app

COPY package.json yarn.lock ./
RUN yarn install

COPY . ./

CMD npm run serve
