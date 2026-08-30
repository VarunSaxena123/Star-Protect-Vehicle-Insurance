import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { ApiService } from './api.service';
import { LoginRequest, LoginResponse, CurrentUser } from '../models/login.model';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private currentUserSubject = new BehaviorSubject<CurrentUser | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private apiService: ApiService, private router: Router) {
    const storedUser = localStorage.getItem('currentUser');
    if (storedUser) {
      this.currentUserSubject.next(JSON.parse(storedUser));
    }
  }

  login(loginRequest: LoginRequest): Observable<LoginResponse> {
    return this.apiService.post<LoginResponse>('/auth/login', loginRequest);
  }

  setCurrentUser(user: CurrentUser): void {
    localStorage.setItem('currentUser', JSON.stringify(user));
    this.currentUserSubject.next(user);
  }

  getCurrentUser(): CurrentUser | null {
    return this.currentUserSubject.value;
  }

  isLoggedIn(): boolean {
    return this.currentUserSubject.value !== null;
  }

  isAdmin(): boolean {
    const user = this.currentUserSubject.value;
    return user !== null && user.role === 'admin';
  }

  isUnderwriter(): boolean {
    const user = this.currentUserSubject.value;
    return user !== null && user.role === 'underwriter';
  }

  logout(): void {
    // Clear localStorage
    localStorage.removeItem('currentUser');
    // Clear the subject
    this.currentUserSubject.next(null);
    // Force navigation to landing page (root route)
    // Use setTimeout to ensure navigation happens after cleanup
    setTimeout(() => {
      this.router.navigate(['/']).then(() => {
        // Optional: reload page to ensure complete reset
        window.location.href = '/';
      });
    }, 0);
  }

  getUserId(): string {
    const user = this.currentUserSubject.value;
    return user ? user.userId : '';
  }

  getUserName(): string {
    const user = this.currentUserSubject.value;
    return user ? user.name : '';
  }
}