import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { CookieService } from 'ngx-cookie-service';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  baseUrl = environment.apiUrl;
  authUrl = this.baseUrl + "/api/v1/auth";
  signupUrl = this.authUrl + "/signup";
  signinUrl = this.authUrl + "/signin";
  refreshUrl = this.authUrl + "/refresh";

  constructor(private client: HttpClient, private cookieService: CookieService) { }

  public signup(request: SignupRequest): Observable<HttpResponse<AuthResponse>> {
    return this.client.post<AuthResponse>(this.signupUrl, request, {
      observe: 'response', withCredentials: true
    });
  }

  public refresh(): Observable<HttpResponse<AuthResponse>> {
    return this.client.post<AuthResponse>(this.refreshUrl, { 'refreshToken': this.cookieService.get("refresh_token") }, {
      observe: 'response', withCredentials: true
    });
  }

  public login(request: SignInRequest): Observable<HttpResponse<AuthResponse>> {
    return this.client.post<AuthResponse>(this.signinUrl, request, {
      observe: 'response', withCredentials: true
    });
  }
}

export interface SignupRequest {
  username: string,
  email: string,
  password: string
}

export interface AuthResponse {
  token: string,
  expiration: string
}

export interface SignInRequest {
  username: string, password: string
}

export interface RefreshTokenRequest {
  refreshToken: string
}