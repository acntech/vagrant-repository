#!/bin/sh

if [ -z "${API_URL}" ]; then
  echo "Environment variable API_URL is not set"
  exit 1
fi

sed -i 's#_API_URL_#'"${API_URL}"'#g' /etc/nginx/nginx.conf

nginx;