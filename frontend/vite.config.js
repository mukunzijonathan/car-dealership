import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      "/auth": "http://localhost:8080",
      "/oauth2": "http://localhost:8080",
      "/login": "http://localhost:8080",
      "/car": "http://localhost:8080",
      "/customer": "http://localhost:8080",
      "/employee": "http://localhost:8080",
      "/sale": "http://localhost:8080",
      "/notification": "http://localhost:8080",
    },
  },
});
