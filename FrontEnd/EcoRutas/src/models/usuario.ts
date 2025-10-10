import { Ruta } from './ruta';
import { Alquiler } from './alquiler';
import { HistorialRuta } from './historial-ruta';
import { Logro } from './logro';

export interface Usuario {
  id_usuario?: number;
  nombre: string;
  apellido: string;
  correo: string;
  password?: string;
  rol: 'USUARIO' | 'ADMIN';
  fecha_registro?: string;
  rutas?: Ruta[];
  alquileres?: Alquiler[];
  historialRutas?: HistorialRuta[];
  logros?: Logro[];
}
