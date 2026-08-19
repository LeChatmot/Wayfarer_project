import { defineConfig, devices } from '@playwright/test';

const isCI = !!process.env["CI"];

export default defineConfig({
  testDir: './e2e',
  fullyParallel: true,
  forbidOnly: isCI,
  retries: isCI ? 2 : 0,
  workers: isCI ? 2 : undefined,
  reporter: isCI
    ? [
      ['list'],
      ['html', { outputFolder: 'playwright-report', open: 'never' }],
      ['junit', { outputFile: 'playwright-report/junit-results.xml' }],
    ]
    : [['html', { open: 'never' }]],
  use: {
    baseURL: process.env["E2E_BASE_URL"] || 'http://localhost:4200',
    trace: 'on-first-retry',
    screenshot: 'only-on-failure',
    video: 'retain-on-failure',
    ignoreHTTPSErrors: true,
  },
  projects: isCI
    ? [{ name: 'chromium', use: { ...devices['Desktop Chrome'] } }]
    : [
      { name: 'chromium', use: { ...devices['Desktop Chrome'] } },
      { name: 'firefox', use: { ...devices['Desktop Firefox'] } },
      { name: 'webkit', use: { ...devices['Desktop Safari'] } },
    ],
});
