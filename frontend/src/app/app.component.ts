import { Component } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { UserService } from './user.service';
@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'math-mastery';

  constructor(private userService: UserService) {
    this.userService.me().subscribe({
      next: (v) => {
        console.log(v.body);
      }
    });

  }
}
