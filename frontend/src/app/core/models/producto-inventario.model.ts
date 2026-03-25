export interface ProductoInventario {
  codigo: number;
  brand: string;
  model: string;
  type: string;
  price: number;
  stock: number;
  stockMinimo: number;
  stockMaximo: number;
  description: string;
  image: string;
}
