import { Usuario } from './usuario';
import { Alquiler } from './alquiler';
import { HistorialRuta } from './historial-ruta';

export interface Ruta {
  id_ruta?: number;
  nombre: string;
  descripcion: string;
  punto_inicio: string;
  punto_fin: string;
  distancia_km: number;
  tipo: 'BICICLETA' | 'SCOOTER' | 'MONOPATIN_ELECTRICO' | 'SEGWAY' | 'CARPOOL';
  estado: 'ACTIVA' | 'INACTIVA';
  fecha_creacion?: string;
  usuario: Usuario;
  alquileres?: Alquiler[];
  historialRutas?: HistorialRuta[];
}
