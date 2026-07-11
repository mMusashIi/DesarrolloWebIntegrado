import { Component, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../../core/auth/auth.service';
import { LoginModalComponent } from '../../../features/auth/login-modal/login-modal.component';
import { RegisterModalComponent } from '../../../features/auth/register-modal/register-modal.component';
import { ProfileModalComponent } from '../../../features/auth/profile-modal/profile-modal.component';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, RouterModule, LoginModalComponent, RegisterModalComponent, ProfileModalComponent],
  templateUrl: './header.component.html'
})
export class HeaderComponent {
  auth = inject(AuthService);
  showLoginModal = signal(false);
  showRegisterModal = signal(false);
  showProfileModal = signal(false);

  openLogin(): void { this.showLoginModal.set(true); }
  closeLogin(): void { this.showLoginModal.set(false); }
  openRegister(): void { this.showRegisterModal.set(true); }
  closeRegister(): void { this.showRegisterModal.set(false); }

  onLoginSuccess(): void {
    this.showLoginModal.set(false);
  }

  onRegisterSuccess(): void {
    this.showRegisterModal.set(false);
  }
}
