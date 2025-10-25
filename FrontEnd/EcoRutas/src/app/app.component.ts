import { Component } from '@angular/core';
import { Router, RouterOutlet, RouterModule } from '@angular/router';
@Component({
  selector: 'app-root',
  standalone: true, 
  imports: [RouterOutlet, RouterModule], 
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  constructor(private router: Router) {}

  cerrarSesion(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('usuario');
    this.router.navigate(['/login']);
  }
}
