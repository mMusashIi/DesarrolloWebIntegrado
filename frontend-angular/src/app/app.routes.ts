import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { adminGuard } from './core/guards/admin.guard';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./features/home/home.component').then(m => m.HomeComponent)
  },
  {
    path: 'paquetes',
    loadComponent: () => import('./features/paquetes/paquetes.component').then(m => m.PaquetesComponent)
  },
  {
    path: 'reservas',
    canActivate: [authGuard],
    loadComponent: () => import('./features/reservas/reservas.component').then(m => m.ReservasComponent)
  },
  {
    path: 'pago-exitoso',
    loadComponent: () => import('./features/pago-resultado/pago-resultado.component').then(m => m.PagoResultadoComponent)
  },
  {
    path: 'pago-fallido',
    loadComponent: () => import('./features/pago-resultado/pago-resultado.component').then(m => m.PagoResultadoComponent)
  },
  {
    path: 'pago-pendiente',
    loadComponent: () => import('./features/pago-resultado/pago-resultado.component').then(m => m.PagoResultadoComponent)
  },
  {
    path: 'nosotros',
    loadComponent: () => import('./features/nosotros/nosotros.component').then(m => m.NosotrosComponent)
  },
  {
    path: 'contacto',
    loadComponent: () => import('./features/contacto/contacto.component').then(m => m.ContactoComponent)
  },
  {
    path: 'admin',
    canActivate: [authGuard, adminGuard],
    loadComponent: () => import('./features/admin/admin.component').then(m => m.AdminComponent),
    children: [
      {
        path: '',
        loadComponent: () => import('./features/admin/dashboard/dashboard.component').then(m => m.DashboardComponent)
      },
      {
        path: 'paquetes',
        loadComponent: () => import('./features/admin/paquetes/admin-paquetes.component').then(m => m.AdminPaquetesComponent)
      },
      {
        path: 'inventario',
        loadComponent: () => import('./features/admin/inventario/admin-inventario.component').then(m => m.AdminInventarioComponent)
      },
      {
        path: 'reservas',
        loadComponent: () => import('./features/admin/reservas/admin-reservas.component').then(m => m.AdminReservasComponent)
      },
      {
        path: 'usuarios',
        loadComponent: () => import('./features/admin/usuarios/admin-usuarios.component').then(m => m.AdminUsuariosComponent)
      },
      {
        path: 'lugares',
        loadComponent: () => import('./features/admin/lugares/admin-lugares.component').then(m => m.AdminLugaresComponent)
      },
      {
        path: 'reportes',
        loadComponent: () => import('./features/admin/reportes/admin-reportes.component').then(m => m.AdminReportesComponent)
      }
    ]
  },
  {
    path: '**',
    redirectTo: ''
  }
];
