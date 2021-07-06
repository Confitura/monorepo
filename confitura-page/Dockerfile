FROM node:10

WORKDIR /opt/app

COPY package.json yarn.lock ./
RUN yarn install
RUN yarn global add @angular/cli@7

COPY . ./

CMD ng serve --aot --host 0.0.0.0
