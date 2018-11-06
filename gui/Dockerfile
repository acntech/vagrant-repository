#
# Build
#
FROM node

WORKDIR /tmp

COPY . .

RUN npm install \
 && npm run build

#
# Runtime
#
FROM nginx:alpine

ARG SERVER_PORT=80
ARG SERVER_HOST=_
ARG API_URL

COPY ./etc/nginx.conf /etc/nginx/nginx.conf

RUN sed -i 's#_API_URL_#'"${API_URL}"'#g' /etc/nginx/nginx.conf \
 && sed -i 's#_SERVER_PORT_#'"${SERVER_PORT}"'#g' /etc/nginx/nginx.conf \
 && sed -i 's#_SERVER_HOST_#'"${SERVER_HOST}"'#g' /etc/nginx/nginx.conf \
 && cat /etc/nginx/nginx.conf

WORKDIR /usr/share/nginx/html

COPY --from=0 /tmp/dist/* ./

EXPOSE 80:${SERVER_PORT}

CMD ["nginx", "-g", "daemon off;"]
