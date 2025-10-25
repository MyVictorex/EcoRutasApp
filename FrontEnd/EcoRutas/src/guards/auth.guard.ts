import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  constructor(private router: Router) {}

  canActivate(): boolean {
    const token = localStorage.getItem('token');

    if (token && !this.isTokenExpired(token)) {
      return true; 
    } else {
      this.logout();
      return false; 
    }
  }


  private isTokenExpired(token: string): boolean {
    const payload = JSON.parse(atob(token.split('.')[1]));
    const currentTime = Math.floor(Date.now() / 1000);
    return currentTime >= payload.exp;
  }

  private logout(): void {
    localStorage.removeItem('token');
    this.router.navigate(['/login']);
  }
}
