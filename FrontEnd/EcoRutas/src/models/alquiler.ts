import { Usuario } from './usuario';
import { Vehiculo } from './vehiculo';
import { Ruta } from './ruta';

export interface Alquiler {
  id_alquiler?: number;
  usuario: Usuario;
  vehiculo: Vehiculo;
  ruta: Ruta;
  fecha_inicio?: string;
  fecha_fin?: string;
  costo: number;
  estado: 'EN_CURSO' | 'FINALIZADO';
}
