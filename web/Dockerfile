# build environment
FROM node:12.9.1-alpine as build
WORKDIR /app
ENV PATH /app/node_modules/.bin:$PATH
COPY package.json /app/package.json
RUN npm install --silent
RUN npm install react-scripts -g --silent
COPY . /app
ARG base_domain
ARG api_base_domain=https://adesso.energy
ENV SASS_PATH=node_modules:src
ENV PUBLIC_URL=$base_domain
ENV REACT_APP_API_BASE_DOMAIN=$api_base_domain
RUN npm run build

# production environment
FROM nginx:1.16.0-alpine
COPY --from=build /app/build /usr/share/nginx/html
RUN rm /etc/nginx/conf.d/default.conf
COPY nginx/nginx.conf /etc/nginx/conf.d
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]