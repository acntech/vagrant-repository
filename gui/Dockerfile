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

COPY ./etc/nginx.conf /etc/nginx/nginx.conf
COPY ./bin/entrypoint.sh /entrypoint.sh

WORKDIR /usr/share/nginx/html

COPY --from=0 /tmp/dist/* ./

EXPOSE 80

ENTRYPOINT [ "/entrypoint.sh" ]
