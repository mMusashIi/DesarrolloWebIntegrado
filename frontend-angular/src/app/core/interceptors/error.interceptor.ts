import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { JwtService } from '../auth/jwt.service';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const jwt = inject(JwtService);
  const router = inject(Router);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401) {
        jwt.clearSession();
        router.navigate(['/']);
      }
      return throwError(() => error);
    })
  );
};
