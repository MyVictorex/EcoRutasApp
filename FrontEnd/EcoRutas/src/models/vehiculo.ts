import { Alquiler } from './alquiler';

export interface Vehiculo {
  id_vehiculo?: number;
  tipo: 'BICICLETA' | 'SCOOTER' | 'MONOPATIN_ELECTRICO' | 'SEGWAY' | 'CARPOOL';
  codigo_qr: string;
  disponible: boolean;
  ubicacion_actual: string;
  fecha_registro?: string;
  alquileres?: Alquiler[];
}
