import { Component } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  // this component is very similar to SignupComponent - I wonder if its possible to share some code between them
  hide = true;
  buttonDisabled = true;
  form: FormGroup;
  errorText = "";

  constructor(private authService: AuthService, private router: Router) {
    this.form = new FormGroup({
      username: new FormControl('', [Validators.required]),
      password: new FormControl('', [Validators.required])
    });

    this.form.valueChanges.subscribe(() => {
      this.buttonDisabled = this.form.invalid;
    });
  }

  get username() {
    return this.form.get('username');
  }

  get password() {
    return this.form.get('password');
  }

  login() {
    this.errorText = "";

    this.authService.login({
      username: this.form.get('username')!.value,
      password: this.form.get('password')!.value
    }).subscribe({
      next: (v) => {
        localStorage.setItem('accessToken', v.body!.token);
        localStorage.setItem('expiresAt', v.body!.expiration);

        this.errorText = "";
        this.router.navigate(['/']);
      },
      error: (e) => {
        let body = JSON.parse(JSON.stringify(e.error));

        // todo: at backend add validators and send errors for each field: e.g. 'username': 'username already taken'...
        // at the frontend handle it more appropriate than printing whole error message 
        this.errorText = body['detail'];
      },
      complete: () => console.info('complete')
    })
  }
}
