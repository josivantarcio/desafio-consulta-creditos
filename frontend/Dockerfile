# ==========================================
# Estágio 1: Build da aplicação Angular
# ==========================================
FROM node:22-alpine AS builder

WORKDIR /app

# Copia manifestos de dependências para cache
COPY package*.json ./
RUN npm ci

# Copia código-fonte e realiza o build de produção
COPY . .
RUN npm run build

# ==========================================
# Estágio 2: Nginx para servir os arquivos estáticos
# ==========================================
FROM nginx:alpine

# Copia o bundle compilado do Angular
COPY --from=builder /app/dist/frontend/browser /usr/share/nginx/html

# Copia a configuração customizada do Nginx com suporte a SPA e proxy reverso
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
