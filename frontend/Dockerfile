# ---------- Build stage ----------
FROM node:22-alpine AS build
LABEL author="littlebizsolutions"
WORKDIR /app

# Copy package manifests first for layer caching
COPY package*.json ./

# If a lockfile exists, use npm ci; otherwise fallback to npm install
# (npm ci is faster and reproducible; npm install works when no lockfile)
RUN if [ -f package-lock.json ]; then npm ci; else npm install; fi

# Copy the rest of the app and build
COPY . .
RUN npm run build -- --configuration=production --output-path=dist/app

# ---------- Runtime stage ----------
FROM nginx:1.27-alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist/app/ /usr/share/nginx/html/
EXPOSE 80
HEALTHCHECK --interval=30s --timeout=3s CMD wget -qO- http://localhost/ || exit 1
