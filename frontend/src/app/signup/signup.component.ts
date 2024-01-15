import { AfterViewInit, Component, ElementRef, ViewChild } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatHint } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { AuthResponse, AuthService } from '../auth.service';
import { Observable } from 'rxjs';
import { routes } from '../app-routing.module';
import { Router } from '@angular/router';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css']
})
export class SignupComponent {
  hide = true;
  buttonDisabled = true;
  form: FormGroup;
  errorText = "";

  constructor(private authService: AuthService, private router: Router) {
    this.form = new FormGroup({
      username: new FormControl('', [Validators.required]),
      email: new FormControl('', [Validators.required, Validators.email, Validators.pattern('^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$')]),
      password: new FormControl('', [Validators.required, Validators.minLength(8)])
    });

    this.form.valueChanges.subscribe(() => {
      this.buttonDisabled = this.form.invalid;
    });
  }

  get username() {
    return this.form.get('username');
  }

  get email() {
    return this.form.get('email');
  }

  get password() {
    return this.form.get('password');
  }

  signup() {
    this.authService.signup({
      username: this.form.get('username')!.value,
      email: this.form.get('email')!.value,
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
