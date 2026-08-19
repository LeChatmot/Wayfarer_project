import { test, expect } from '@playwright/test';

test.describe('Parcours de disponibilite applicative', () => {
  test("la page d'accueil se charge et affiche la carte", async ({ page }) => {
    await page.goto('/');
    await expect(page.locator('.leaflet-container')).toBeVisible({ timeout: 15000 });
  });

  test('les tuiles cartographiques sont chargees depuis le fournisseur', async ({ page }) => {
    await page.goto('/');
    await expect(page.locator('.leaflet-tile-loaded').first()).toBeVisible({
      timeout: 20000,
    });
  });

  test("le formulaire d'inscription est accessible et valide les entrees", async ({ page }) => {
    await page.goto('/register');
    const submit = page.getByRole('button', { name: /inscri/i });
    await expect(submit).toBeVisible();
    await submit.click();
    await expect(page.locator('mat-error, .error, [role="alert"]').first()).toBeVisible({
      timeout: 5000,
    });
  });

  test("l'API backend repond via le reverse proxy", async ({ request }) => {
    const response = await request.get('/api/map-providers/list');
    expect([200, 401, 403]).toContain(response.status());
  });
});
