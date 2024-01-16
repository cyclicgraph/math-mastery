import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpErrorResponse
} from '@angular/common/http';
import { Observable, catchError, switchMap, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';
import { CookieService } from 'ngx-cookie-service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(private authService: AuthService, private cookieService: CookieService) { }

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    if (request.url.includes('/api/v1/auth')) {
      return next.handle(request);
    }

    let token = localStorage.getItem('accessToken');
    if (token) {
      const cloned = this.enrich(request, token);

      return next.handle(cloned).pipe(
        catchError((error) => {
          if (error.status === 401 || error.status === 403) {
            return this.handle401Error(cloned, next);
          }
          return throwError(() => new Error(error));
        })
      );
    } else if (this.cookieService.get("refresh_token")) {
      // Use switchMap to wait for the refresh token response
      return this.authService.refresh().pipe(
        switchMap((v) => {
          let token = v.body?.token as string;
          localStorage.setItem('accessToken', v.body!.token);
          localStorage.setItem('expiresAt', v.body!.expiration);

          const cloned = this.enrich(request, token);

          // Handle the original request with the new token
          return next.handle(cloned).pipe(
            catchError((error) => {
              if (error.status === 401 || error.status === 403) {
                return this.handle401Error(cloned, next);
              }
              return throwError(() => new Error(error));
            })
          );
        }),
        catchError((error) => {
          // Handle errors from the refresh call
          return throwError(() => new Error(error));
        })
      );
    }
    else {
      return next.handle(
        request.clone({
          withCredentials: true,
        })
      );
    }
  }

  private handle401Error(
    request: HttpRequest<unknown>,
    next: HttpHandler
  ): Observable<HttpEvent<unknown>> {
    return this.authService.refresh().pipe(
      switchMap((token: any) => {
        return next.handle(this.enrich(request, token.accessToken));
      }),
      catchError((error: HttpErrorResponse) => {
        localStorage.setItem('accessToken', '');
        localStorage.setItem('expiresAt', '');

        return throwError(() => error);
      })
    );
  }

  private enrich(
    request: HttpRequest<unknown>,
    token: string
  ): HttpRequest<unknown> {
    return request.clone({
      headers: request.headers.set('Authorization', 'Bearer ' + token),
      withCredentials: true,
    });
  }

}
