import { Component, inject, signal } from '@angular/core';
import { NavigationEnd, Router, RouterOutlet } from '@angular/router';
import { filter } from 'rxjs/operators';
import { HeaderComponent } from './shared/components/header/header.component';
import { FooterComponent } from './shared/components/footer/footer.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, HeaderComponent, FooterComponent],
  template: `
    @if (!isAdminRoute()) { <app-header></app-header> }
    <main [class.page-with-header]="hasHeaderOffset()">
      <router-outlet></router-outlet>
    </main>
    @if (!isAdminRoute()) { <app-footer></app-footer> }
  `
})
export class AppComponent {
  private router = inject(Router);
  isAdminRoute = signal(this.router.url.startsWith('/admin'));
  hasHeaderOffset = signal(this.needsHeaderOffset(this.router.url));

  constructor() {
    this.router.events.pipe(filter(event => event instanceof NavigationEnd)).subscribe(event => {
      const url = (event as NavigationEnd).urlAfterRedirects;
      this.isAdminRoute.set(url.startsWith('/admin'));
      this.hasHeaderOffset.set(this.needsHeaderOffset(url));
    });
  }

  private needsHeaderOffset(url: string): boolean {
    const path = url.split(/[?#]/, 1)[0];
    return path !== '/' && path !== '' && !path.startsWith('/admin');
  }
}
