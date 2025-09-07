// src/app/core/guards/role.guard.ts
import { inject } from '@angular/core';
import { CanMatchFn, Router } from '@angular/router';
import {TokenStorageService} from "src/app/services/token-storage.service";

export const roleGuard = (allowed: string[]): CanMatchFn => () => {
  const store = inject(TokenStorageService);
  const router = inject(Router);
  const ok = store.roles.some(r => allowed.includes(r));
  if (!ok) router.navigate(['/app']);
  return ok;
};
