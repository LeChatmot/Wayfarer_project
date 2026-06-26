FROM node:26-alpine AS builder

WORKDIR /app

COPY package.json package-lock.json* ./

RUN --mount=type=cache, target=/root/.npm npm ci

COPY . .

RUN npm run build
