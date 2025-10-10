import { Usuario } from './usuario';

export interface Logro {
  id_logro?: number;
  nombre: string;
  descripcion: string;
  puntos: number;
  usuarios?: Usuario[];
}
