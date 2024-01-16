import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  baseUrl = environment.apiUrl;
  userUrl = this.baseUrl + "/api/v1/user";
  meUrl = this.userUrl + "/me";

  constructor(private client: HttpClient) { }

  public me() {
    return this.client.get<UserCompact>(this.meUrl, {
      observe: 'response', withCredentials: true
    });
  }
}

export interface UserCompact {
  id: string,
  username: string,
  roles: string[],
  challengeRating: number,
  peakRating: number
} 