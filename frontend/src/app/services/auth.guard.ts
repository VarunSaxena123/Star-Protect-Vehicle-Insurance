import { Injectable } from '@angular/core';
import { CanActivate, Router, ActivatedRouteSnapshot } from '@angular/router';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): boolean {
    const expectedRole = route.data['role'];
    const currentUser = this.authService.getCurrentUser();

    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['/login']);
      return false;
    }

    if (expectedRole && currentUser?.role !== expectedRole) {
      if (currentUser?.role === 'admin') {
        this.router.navigate(['/admin']);
      } else if (currentUser?.role === 'underwriter') {
        this.router.navigate(['/underwriter']);
      } else {
        this.router.navigate(['/login']);
      }
      return false;
    }

    return true;
  }
}