import { Component } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'math-mastery';

  isAuthenticated(): boolean {
    var expiresAt = localStorage.getItem('expiresAt');
    var accessToken = localStorage.getItem('accessToken');

    if (expiresAt == null || expiresAt.trim() == '' || accessToken == null || accessToken.trim() == '') return false;
    const expirationDate = new Date(expiresAt);

    return expirationDate > new Date();
  }

  getUsername(): string {
    return localStorage.getItem('username')!;
  }
}
