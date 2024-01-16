import { Component } from '@angular/core';
import { UserService } from '../services/user.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent {
   text: string = "Hello!";

  constructor(private userService: UserService) {
    this.userService.me().subscribe({
      next: (v) => {
        this.text = "Hello " + v.body?.username;
      }
    });

  }

}
