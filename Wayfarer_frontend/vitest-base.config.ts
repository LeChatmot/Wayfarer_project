import { defineConfig } from 'vitest/config';

export default defineConfig({
  test: {
    globals: true,
    environment: 'jsdom',
    include: ['**/*.{spec,test}.{js,ts}'],
    coverage: {
      enabled: true,
      provider: 'v8',
    },
    environmentOptions: {
      jsdom: {
        resources: 'usable',
      },
    },
  },
});
