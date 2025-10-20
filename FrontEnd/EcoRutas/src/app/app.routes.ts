import { Routes } from '@angular/router';

// 🧩 Login y Home
import { LoginComponent } from './login/login/login.component';
import { HomeComponent } from './pages/home/home.component'; 

// 🚲 Alquileres
import { ListarComponent as AlquilerListar } from './pages/alquileres/listar/listar.component';
import { FormComponent as AlquilerForm } from './pages/alquileres/form/form.component';

// 🗺️ Rutas
import { ListarComponent as RutaListar } from './pages/rutas/listar/listar.component';
import { FormComponent as RutaForm } from './pages/rutas/form/form.component';

// 👤 Usuarios
import { ListarComponent as UsuarioListar } from './pages/usuarios/listar/listar.component';
import { FormComponent as UsuarioForm } from './pages/usuarios/form/form.component';

// 🚘 Vehículos
import { ListarComponent as VehiculoListar } from './pages/vehiculos/listar/listar.component';
import { FormComponent as VehiculoForm } from './pages/vehiculos/form/form.component';

export const routes: Routes = [
  // 🏠 Rutas principales
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'home', component: HomeComponent  },

  // 🚲 Alquileres
  { path: 'alquileres', component: AlquilerListar },
  { path: 'alquileres/nuevo', component: AlquilerForm },
  { path: 'alquileres/editar/:id', component: AlquilerForm },

  // 🗺️ Rutas
  { path: 'rutas', component: RutaListar },
  { path: 'rutas/nuevo', component: RutaForm },
  { path: 'rutas/editar/:id', component: RutaForm },

  // 👤 Usuarios
  { path: 'usuarios', component: UsuarioListar },
  { path: 'usuarios/nuevo', component: UsuarioForm },
  { path: 'usuarios/editar/:id', component: UsuarioForm },

  // 🚘 Vehículos
  { path: 'vehiculos', component: VehiculoListar },
  { path: 'vehiculos/nuevo', component: VehiculoForm },
  { path: 'vehiculos/editar/:id', component: VehiculoForm },
];
