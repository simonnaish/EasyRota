import {HttpClient} from "@angular/common/http";
import {inject, Injectable} from '@angular/core';
import {Observable, tap} from "rxjs";
import {
  AuthResponse,
  LoginRequest,
  MeResponse,
  RegistrationRequest,
  RegistrationResponse,
  TokenRequest
} from "src/app/models/auth";
import {TokenStorageService} from "src/app/services/token-storage.service";

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly apiUrl = 'http://localhost:8080/api/auth';
  private readonly _http: HttpClient = inject(HttpClient);
  private readonly _store: TokenStorageService = inject(TokenStorageService);

  constructor() {
  }

  public register(req: RegistrationRequest): Observable<RegistrationResponse> {
    return this._http.post<RegistrationResponse>(`${this.apiUrl}/register`, req);
  }

  login(req: LoginRequest): Observable<AuthResponse> {
    return this._http.post<AuthResponse>(`${this.apiUrl}/login`, req).pipe(
      tap(res => this._store.setSession(res.accessToken, res.refreshToken,
        { roles: res.roles, email: res.email, userId: res.userId }))
    );
  }

  refresh(): Observable<AuthResponse> {
    const token = this._store.refreshToken;
    if (!token) throw new Error('No refresh token');
    return this._http.post<AuthResponse>(`${this.apiUrl}/refresh`, { refreshToken: token } as TokenRequest).pipe(
      tap(res => this._store.setSession(res.accessToken, res.refreshToken,
        { roles: res.roles, email: res.email, userId: res.userId }))
    );
  }

  logout(): Observable<void> {
    const token = this._store.refreshToken;
    // Clear locally regardless of server response
    return this._http.post<void>(`${this.apiUrl}/logout`, { refreshToken: token }).pipe(
      tap(() => this._store.clear())
    );
  }

  me(): Observable<MeResponse> {
    return this._http.get<MeResponse>(`${this.apiUrl}/me`).pipe(
      tap(res => {
        // keep roles/email in sync if needed
        this._store.setSession(this._store.accessToken ?? '', this._store.refreshToken ?? '', {
          roles: res.roles, email: res.email, userId: res.userId
        });
      })
    );
  }

  isAuthenticated(): boolean { return this._store.isAuthenticated(); }
  roles(): string[] { return this._store.roles; }
}

