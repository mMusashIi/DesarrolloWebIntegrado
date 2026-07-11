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
  onImgError(event: Event): void {
    (event.target as HTMLImageElement).src = '/images/placeholder.jpg';
  }
}
