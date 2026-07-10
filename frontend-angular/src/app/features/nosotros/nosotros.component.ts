import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-nosotros',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './nosotros.component.html'
})
export class NosotrosComponent {
  equipo = [
    { nombre: 'Carlos Mamani', rol: 'Guía Principal', foto: '👨‍💼', descripcion: 'Más de 15 años guiando viajeros por el Cusco.' },
    { nombre: 'María Quispe', rol: 'Coordinadora de Tours', foto: '👩‍💼', descripcion: 'Especialista en logística y atención al cliente.' },
    { nombre: 'Pedro Huanca', rol: 'Guía de Montaña', foto: '🧗', descripcion: 'Experto en rutas de alta montaña y trekking.' }
  ];

  valores = [
    { icono: '🌿', titulo: 'Turismo Sostenible', desc: 'Cuidamos el medio ambiente y las comunidades locales.' },
    { icono: '🤝', titulo: 'Compromiso', desc: 'Tu satisfacción es nuestra prioridad en cada tour.' },
    { icono: '🏆', titulo: 'Experiencia', desc: 'Más de 10 años llevando viajeros al corazón del Perú.' },
    { icono: '💯', titulo: 'Calidad', desc: 'Servicios de primera con guías certificados y equipos seguros.' }
  ];
}
