# Etapa de construcción
FROM node:22-alpine
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# Etapa de servidor
FROM nginx:alpine
# COPIAMOS TU CONFIGURACIÓN PERSONALIZADA DE NGINX
COPY nginx.conf /etc/nginx/conf.d/default.conf
# Copiamos los archivos construidos
COPY --from=0 /app/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]