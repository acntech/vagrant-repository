FROM node

ARG SERVER_PORT=3000

WORKDIR /usr/src/app

COPY package.json ./

RUN npm install

COPY . .

EXPOSE 3000:${SERVER_PORT}

CMD [ "npm", "run", "dev-start" ]