// src/app/core/auth/auth.interceptor.ts
import {
  HttpInterceptorFn,
  HttpRequest,
  HttpEvent
} from '@angular/common/http';
import { inject } from '@angular/core';
import {
  BehaviorSubject,
  Observable,
  throwError
} from 'rxjs';
import {
  catchError,
  filter,
  finalize,
  switchMap,
  take,
  tap
} from 'rxjs/operators';
import {AuthService} from "src/app/services/auth.service";
import {TokenStorageService} from "src/app/services/token-storage.service";


let refreshing = false;
const refreshSubject = new BehaviorSubject<string | null>(null);

export const authInterceptor: HttpInterceptorFn = (
  req,
  next
): Observable<HttpEvent<unknown>> => {
  const store = inject(TokenStorageService);
  const auth = inject(AuthService);

  const addAuth = (r: HttpRequest<any>, token: string | null) =>
    token ? r.clone({ setHeaders: { Authorization: `Bearer ${token}` } }) : r;

  const isAuthRoute =
    req.url.includes('/api/auth/login') || req.url.includes('/api/auth/refresh');

  return next(addAuth(req, store.accessToken)).pipe(
    catchError(err => {
      // If not 401 or it's an auth route, just fail
      if (err.status !== 401 || isAuthRoute) {
        return throwError(() => err);
      }

      if (!refreshing) {
        // Start a refresh flow
        refreshing = true;
        refreshSubject.next(null);

        return auth.refresh().pipe(
          tap(res => {
            // publish the new access token to unblock queued requests
            refreshSubject.next(res.accessToken);
          }),
          switchMap(res => next(addAuth(req, res.accessToken))),
          finalize(() => {
            refreshing = false;
          }),
          catchError(refreshErr => {
            // refresh failed → clear session and propagate error
            store.clear();
            return throwError(() => refreshErr);
          })
        );
      }

      // Already refreshing → wait until a token appears, then retry original req
      return refreshSubject.pipe(
        filter((t): t is string => t != null),
        take(1),
        switchMap(token => next(addAuth(req, token)))
      );
    })
  );
};
