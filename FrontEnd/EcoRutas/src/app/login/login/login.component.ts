import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { LoginService } from './loginServices';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  correo: string = '';
  password: string = '';
  errorMsg: string = '';

  constructor(private loginService: LoginService, private router: Router) {}

  onSubmit(): void {
    const credenciales = {
      correo: this.correo,
      password: this.password
    };

    this.loginService.login(credenciales).subscribe({
      next: (response) => {
        localStorage.setItem('token', response.token);
        localStorage.setItem('usuario', JSON.stringify(response.usuario));
        this.router.navigate(['/home']);
      },
      error: (err) => {
        console.error(err);
        this.errorMsg = 'Correo o contraseña incorrectos';
      }
    });
  }
}
