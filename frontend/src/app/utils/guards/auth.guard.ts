// src/app/core/guards/auth.guard.ts
import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import {TokenStorageService} from "src/app/services/token-storage.service";

export const authGuard: CanActivateFn = () => {
  const store = inject(TokenStorageService);
  const router = inject(Router);
  if (store.isAuthenticated()) return true;
  router.navigate(['/auth/login']);
  return false;
};
