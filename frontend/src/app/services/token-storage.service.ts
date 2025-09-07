// src/app/core/auth/token-storage.service.ts
import { Injectable } from '@angular/core';

const ACCESS = 'easyrota_access';
const REFRESH = 'easyrota_refresh';
const ROLES = 'easyrota_roles';
const EMAIL = 'easyrota_email';
const UID = 'easyrota_uid';

@Injectable({ providedIn: 'root' })
export class TokenStorageService {
  setSession(access: string, refresh: string, meta?: { roles?: string[]; email?: string; userId?: number }) {
    localStorage.setItem(ACCESS, access);
    localStorage.setItem(REFRESH, refresh);
    if (meta?.roles) localStorage.setItem(ROLES, JSON.stringify(meta.roles));
    if (meta?.email) localStorage.setItem(EMAIL, meta.email);
    if (meta?.userId != null) localStorage.setItem(UID, String(meta.userId));
  }
  clear() {
    localStorage.removeItem(ACCESS);
    localStorage.removeItem(REFRESH);
    localStorage.removeItem(ROLES);
    localStorage.removeItem(EMAIL);
    localStorage.removeItem(UID);
  }
  get accessToken(): string | null { return localStorage.getItem(ACCESS); }
  get refreshToken(): string | null { return localStorage.getItem(REFRESH); }
  get roles(): string[] { try { return JSON.parse(localStorage.getItem(ROLES) || '[]'); } catch { return []; } }
  get email(): string | null { return localStorage.getItem(EMAIL); }
  get userId(): number | null { const v = localStorage.getItem(UID); return v ? Number(v) : null; }
  isAuthenticated(): boolean { return !!this.accessToken; } // refine by exp if you decode JWT
}
