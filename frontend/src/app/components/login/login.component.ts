import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { LoginRequest } from '../../models/login.model';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']  // Make sure this file exists
})
export class LoginComponent implements OnInit {
  loginForm: FormGroup;
  loading = false;
  errorMessage = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      userId: ['', Validators.required],
      password: ['', Validators.required],
      role: ['admin', Validators.required]
    });
  }

  ngOnInit(): void {
    if (this.authService.isLoggedIn()) {
      const user = this.authService.getCurrentUser();
      if (user?.role === 'admin') {
        this.router.navigate(['/admin']);
      } else if (user?.role === 'underwriter') {
        this.router.navigate(['/underwriter']);
      }
    }
  }

  onSubmit(): void {
    if (this.loginForm.invalid) {
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    const loginRequest: LoginRequest = this.loginForm.value;

    this.authService.login(loginRequest).subscribe({
      next: (response) => {
        if (response.success && response.userId && response.role) {
          this.authService.setCurrentUser({
            userId: response.userId,
            name: response.name || response.userId,
            role: response.role
          });

          if (response.role === 'admin') {
            this.router.navigate(['/admin']);
          } else {
            this.router.navigate(['/underwriter']);
          }
        } else {
          this.errorMessage = response.message;
        }
        this.loading = false;
      },
      error: (error) => {
        console.error('Login error:', error);
        this.errorMessage = 'Server connection error. Please make sure the backend is running.';
        this.loading = false;
      }
    });
  }
}