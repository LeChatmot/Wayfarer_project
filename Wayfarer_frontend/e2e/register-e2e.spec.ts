import { test, expect } from '@playwright/test';

test.describe('Inscription utilisateur', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/register');
  });

  test('devrait afficher le formulaire d\'inscription', async ({ page }) => {
    await expect(page.getByText('Créer un compte')).toBeVisible();
    await expect(page.getByLabel('Nom d\'utilisateur')).toBeVisible();
    await expect(page.getByLabel('Email')).toBeVisible();
    await expect(page.getByLabel('Mot de passe', { exact: true })).toBeVisible();
    await expect(page.getByLabel('Confirmer le mot de passe')).toBeVisible();
  });

  test('devrait créer un compte avec succès et rediriger vers la carte', async ({ page }) => {
    await page.route('**/api/auth/register', async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({ token: 'fake-jwt-token' })
      });
    });

    await page.getByLabel('Nom d\'utilisateur').fill('randonneur42');
    await page.getByLabel('Email').fill('randonneur42@example.com');
    await page.getByLabel('Mot de passe', { exact: true }).fill('MotDePasse1');
    await page.getByLabel('Confirmer le mot de passe').fill('MotDePasse1');

    await page.getByRole('button', { name: 'S\'inscrire' }).click();

    await expect(page).toHaveURL('/map');
  });

  test('devrait afficher une erreur si le nom d\'utilisateur est vide', async ({ page }) => {
    await page.getByLabel('Email').fill('test@example.com');
    await page.getByLabel('Mot de passe', { exact: true }).fill('MotDePasse1');
    await page.getByLabel('Confirmer le mot de passe').fill('MotDePasse1');

    await page.getByRole('button', { name: 'S\'inscrire' }).click();

    await expect(page.getByText('Le nom d\'utilisateur est requis')).toBeVisible();
  });

  test('devrait afficher une erreur si l\'email est invalide', async ({ page }) => {
    await page.getByLabel('Nom d\'utilisateur').fill('randonneur42');
    await page.getByLabel('Email').fill('email-invalide');
    await page.getByLabel('Mot de passe', { exact: true }).fill('MotDePasse1');
    await page.getByLabel('Confirmer le mot de passe').fill('MotDePasse1');

    await page.getByLabel('Email').blur();

    await expect(page.getByText('Format d\'email invalide')).toBeVisible();
  });

  test('devrait afficher une erreur si le mot de passe est trop faible', async ({ page }) => {
    await page.getByLabel('Nom d\'utilisateur').fill('randonneur42');
    await page.getByLabel('Email').fill('test@example.com');
    await page.getByLabel('Mot de passe', { exact: true }).fill('motdepasse');
    await page.getByLabel('Mot de passe', { exact: true }).blur();

    await expect(page.getByText('Doit contenir une majuscule, une minuscule et un chiffre')).toBeVisible();
  });

  test('devrait désactiver le bouton et afficher un spinner pendant le chargement', async ({ page }) => {
    await page.route('**/api/auth/register', async (route) => {
      await new Promise((resolve) => setTimeout(resolve, 2000));
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({ token: 'fake-jwt-token' })
      });
    });

    await page.getByLabel('Nom d\'utilisateur').fill('randonneur42');
    await page.getByLabel('Email').fill('test@example.com');
    await page.getByLabel('Mot de passe', { exact: true }).fill('MotDePasse1');
    await page.getByLabel('Confirmer le mot de passe').fill('MotDePasse1');

    const submitButton = page.locator('button[type="submit"]');
    await submitButton.click();

    await expect(submitButton).toBeDisabled();
    await expect(submitButton.locator('mat-spinner')).toBeVisible();
  });

  test('devrait naviguer vers la page de connexion via le lien', async ({ page }) => {
    await page.getByRole('link', { name: 'Se connecter' }).click();
    await expect(page).toHaveURL('/login');
  });
});
