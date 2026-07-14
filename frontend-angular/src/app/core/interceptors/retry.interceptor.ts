import { HttpInterceptorFn } from '@angular/common/http';
import { retry, timer } from 'rxjs';

export const retryInterceptor: HttpInterceptorFn = (req, next) => {
  return next(req).pipe(
    retry({
      count: 4,
      delay: (error, attempt) => {
        // Retry on gateway/network errors that indicate backend not ready yet
        if (error.status === 502 || error.status === 503 || error.status === 0) {
          return timer(attempt * 1500);
        }
        throw error;
      }
    })
  );
};
