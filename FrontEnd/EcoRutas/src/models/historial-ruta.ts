import { Usuario } from './usuario';
import { Ruta } from './ruta';

export interface HistorialRuta {
  id_historial?: number;
  usuario: Usuario;
  ruta: Ruta;
  fecha_inicio?: string;
  fecha_fin?: string;
  distancia_recorrida: number;
  duracion_minutos: number;
  modo_transporte: 'BICICLETA' | 'SCOOTER' | 'CAMINATA' | 'CARPOOL';
  co2_ahorrado: number;
}
