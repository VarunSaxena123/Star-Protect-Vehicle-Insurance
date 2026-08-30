import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { ApiService } from '../../services/api.service';
import { Underwriter, UnderwriterSearchResult } from '../../models/underwriter.model';
import { InsurancePolicy, DashboardStats } from '../../models/insurance-policy.model';

@Component({
  selector: 'app-admin-dashboard',
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css']
})
export class AdminDashboardComponent implements OnInit {
  currentView = 'dashboard';
  adminName = 'Admin';
  loading = false;

  // Dashboard data
  dashboardStats: DashboardStats = {};
  recentPolicies: InsurancePolicy[] = [];

  // Register data
  registerData = {
    name: '',
    dob: '',
    joiningDate: '',
    password: ''
  };
  confirmPassword = '';
  registerMessage = '';
  registerSuccess = false;

  // Search data
  searchId = '';
  searchResult: UnderwriterSearchResult | null = null;

  // Update password data
  updateData = {
    underwriterId: '',
    newPassword: '',
    confirmPassword: ''
  };
  updateMessage = '';
  updateSuccess = false;

  // Delete data
  deleteId = '';
  deleteMessage = '';
  deleteSuccess = false;

  // Lists
  underwriters: Underwriter[] = [];
  allPolicies: InsurancePolicy[] = [];
  pendingPolicies: InsurancePolicy[] = [];

  constructor(
    private authService: AuthService,
    private apiService: ApiService,
    private router: Router
  ) {}

  ngOnInit(): void {
    const user = this.authService.getCurrentUser();
    if (user) {
      this.adminName = user.name;
    }
    this.loadDashboard();
  }

  getTodayDate(): string {
    const today = new Date();
    const year = today.getFullYear();
    const month = String(today.getMonth() + 1).padStart(2, '0');
    const day = String(today.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  calculateAge(dob: string): number {
    const birthDate = new Date(dob);
    const today = new Date();
    let age = today.getFullYear() - birthDate.getFullYear();
    const monthDiff = today.getMonth() - birthDate.getMonth();
    if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
      age--;
    }
    return age;
  }

  getStatusClass(status: string): string {
    const statusMap: { [key: string]: string } = {
      'pending': 'status-pending',
      'approved': 'status-approved',
      'rejected': 'status-rejected',
      'closed': 'status-closed'
    };
    return statusMap[status] || 'status-active';
  }

  loadContent(view: string): void {
    this.currentView = view;
    this.loading = true;

    switch (view) {
      case 'dashboard':
        this.loadDashboard();
        break;
      case 'viewAll':
        this.loadAllUnderwriters();
        break;
      case 'viewPolicies':
        this.loadAllPolicies();
        break;
      case 'approvePolicies':
        this.loadPendingPolicies();
        break;
      default:
        this.loading = false;
        break;
    }
  }

  loadDashboard(): void {
    this.apiService.get<DashboardStats>('/admin/dashboard/stats').subscribe({
      next: (stats) => {
        this.dashboardStats = stats;
        this.apiService.get<InsurancePolicy[]>('/admin/policies/all').subscribe({
          next: (policies) => {
            this.recentPolicies = policies.slice(0, 5);
            this.loading = false;
          },
          error: () => {
            this.loading = false;
          }
        });
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  loadAllUnderwriters(): void {
    this.apiService.get<Underwriter[]>('/admin/underwriter/all').subscribe({
      next: (data) => {
        this.underwriters = data;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  loadAllPolicies(): void {
    this.apiService.get<InsurancePolicy[]>('/admin/policies/all').subscribe({
      next: (data) => {
        this.allPolicies = data;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  loadPendingPolicies(): void {
    this.apiService.get<InsurancePolicy[]>('/admin/policies/pending').subscribe({
      next: (data) => {
        this.pendingPolicies = data;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  registerUnderwriter(): void {
    if (this.registerData.password !== this.confirmPassword) {
      this.registerMessage = 'Passwords do not match!';
      this.registerSuccess = false;
      return;
    }

    this.loading = true;
    this.registerData.joiningDate = this.getTodayDate();

    this.apiService.post<any>('/admin/underwriter/register', this.registerData).subscribe({
      next: (response) => {
        if (response.status === 'success') {
          this.registerMessage = `Underwriter registered successfully! ID: ${response.underwriterId}`;
          this.registerSuccess = true;
          this.registerData = { name: '', dob: '', joiningDate: '', password: '' };
          this.confirmPassword = '';
        } else {
          this.registerMessage = response.message;
          this.registerSuccess = false;
        }
        this.loading = false;
      },
      error: () => {
        this.registerMessage = 'Error registering underwriter.';
        this.registerSuccess = false;
        this.loading = false;
      }
    });
  }

  searchUnderwriter(): void {
    if (!this.searchId) return;

    this.loading = true;
    this.apiService.get<UnderwriterSearchResult>(`/admin/underwriter/search/${this.searchId}`).subscribe({
      next: (data) => {
        this.searchResult = data;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  updatePassword(): void {
    this.loading = true;
    this.apiService.put<any>('/admin/underwriter/update-password', this.updateData).subscribe({
      next: (response) => {
        if (response.status === 'success') {
          this.updateMessage = response.message;
          this.updateSuccess = true;
          this.updateData = { underwriterId: '', newPassword: '', confirmPassword: '' };
        } else {
          this.updateMessage = response.message;
          this.updateSuccess = false;
        }
        this.loading = false;
      },
      error: () => {
        this.updateMessage = 'Error updating password.';
        this.updateSuccess = false;
        this.loading = false;
      }
    });
  }

  deleteUnderwriter(): void {
    if (!confirm(`Are you sure you want to delete underwriter ${this.deleteId}?`)) return;

    this.loading = true;
    this.apiService.delete<any>(`/admin/underwriter/delete/${this.deleteId}`).subscribe({
      next: (response) => {
        if (response.status === 'success') {
          this.deleteMessage = response.message;
          this.deleteSuccess = true;
          this.deleteId = '';
        } else {
          this.deleteMessage = response.message;
          this.deleteSuccess = false;
        }
        this.loading = false;
      },
      error: () => {
        this.deleteMessage = 'Error deleting underwriter.';
        this.deleteSuccess = false;
        this.loading = false;
      }
    });
  }

  approvePolicy(policyId: string): void {
    if (confirm(`Approve policy ${policyId}?`)) {
      this.loading = true;
      this.apiService.put<any>(`/admin/policies/approve/${policyId}`, {}).subscribe({
        next: (response) => {
          alert(response.message);
          this.loadPendingPolicies();
        },
        error: () => {
          alert('Error approving policy');
          this.loading = false;
        }
      });
    }
  }

  rejectPolicy(policyId: string): void {
    if (confirm(`Reject policy ${policyId}?`)) {
      this.loading = true;
      this.apiService.put<any>(`/admin/policies/reject/${policyId}`, {}).subscribe({
        next: (response) => {
          alert(response.message);
          this.loadPendingPolicies();
        },
        error: () => {
          alert('Error rejecting policy');
          this.loading = false;
        }
      });
    }
  }

  viewPolicyDetails(policyId: string): void {
    this.apiService.get<any>(`/underwriter/policy/${policyId}`).subscribe({
      next: (data) => {
        if (data.found) {
          const p = data.policy;
          alert(`Policy Details:\nPolicy ID: ${p.policyId}\nCustomer: ${p.customerName}\nVehicle: ${p.vehicleNo}\nPremium: ₹${p.premiumAmount}\nStatus: ${p.status}\nFrom: ${p.fromDate}\nTo: ${p.toDate}`);
        } else {
          alert('Policy not found!');
        }
      },
      error: () => {
        alert('Error fetching policy details');
      }
    });
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}