import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'

/// <reference types="vitest/config" />
export default defineConfig({
  plugins: [react(), tailwindcss()],
  server: { host: true, port: 5173 },
  test: {
    globals: true,
    environment: 'jsdom',
    setupFiles: './src/test/setup.ts',
  },
})
