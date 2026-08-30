import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { ApiService } from '../../services/api.service';
import { InsurancePolicy, CreatePolicyRequest, PremiumCalculationRequest, ToDateCalculationRequest } from '../../models/insurance-policy.model';

@Component({
  selector: 'app-underwriter-dashboard',
  templateUrl: './underwriter-dashboard.component.html',
  styleUrls: ['./underwriter-dashboard.component.css']
})
export class UnderwriterDashboardComponent implements OnInit {
  currentView = 'create';
  underwriterName = '';
  underwriterId = '';
  loading = false;

  // Create policy
  policyData: CreatePolicyRequest = {
    vehicleNo: '',
    vehicleType: '',
    customerName: '',
    engineNo: '',
    chassisNo: '',
    phoneNo: '',
    insuranceType: '',
    fromDate: '',
    underwriterId: '',
    vehicleAge: 0,
    tenureYears: 1
  };
  calculatedPremium = '';
  calculatedToDate = '';
  createMessage = '';
  createSuccess = false;

  // View policies
  myPolicies: InsurancePolicy[] = [];
  totalPremium = 0;

  // Renew policy
  renewPolicyId = '';
  renewDetails: InsurancePolicy | null = null;
  renewConfirm = '';
  newPremium = 0;
  renewYears = 1;

  // Change type
  changePolicyId = '';
  changeDetails: InsurancePolicy | null = null;
  changeConfirm = '';

  // Close policy
  closePolicyId = '';
  closeDetails: InsurancePolicy | null = null;
  closeConfirm = '';

  // Payment
  paymentPolicyId = '';
  paymentDetails: InsurancePolicy | null = null;
  paymentMethod = 'credit';
  paymentId = '';

  constructor(
    private authService: AuthService,
    private apiService: ApiService,
    private router: Router
  ) {}

  ngOnInit(): void {
    const user = this.authService.getCurrentUser();
    if (user) {
      this.underwriterName = user.name;
      this.underwriterId = user.userId;
      this.policyData.underwriterId = this.underwriterId;
      this.policyData.fromDate = this.getTodayDate();
    }
  }

  getTodayDate(): string {
    const today = new Date();
    return today.toISOString().split('T')[0];
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

  async updatePreview(): Promise<void> {
    if (this.policyData.vehicleType && this.policyData.insuranceType) {
      const request: PremiumCalculationRequest = {
        vehicleType: this.policyData.vehicleType,
        insuranceType: this.policyData.insuranceType,
        vehicleAge: this.policyData.vehicleAge
      };
      
      this.apiService.post<any>('/underwriter/calculate-premium', request).subscribe({
        next: (data) => {
          this.calculatedPremium = `₹${data.premiumAmount.toLocaleString()}`;
        },
        error: () => {
          let basePremium = this.policyData.vehicleType === '4-wheeler' ? 6500 : 2500;
          let addOns = 0;
          if (this.policyData.insuranceType === 'Full Insurance') addOns += basePremium * 0.4;
          else addOns += basePremium * 0.2;
          if (this.policyData.vehicleAge > 5) addOns += 500;
          if (this.policyData.vehicleAge > 10) addOns += 800;
          this.calculatedPremium = `₹${Math.round(basePremium + addOns).toLocaleString()}`;
        }
      });
    }

    if (this.policyData.fromDate && this.policyData.tenureYears) {
      const request: ToDateCalculationRequest = {
        fromDate: this.policyData.fromDate,
        years: this.policyData.tenureYears
      };
      
      this.apiService.post<any>('/underwriter/calculate-to-date', request).subscribe({
        next: (data) => {
          this.calculatedToDate = data.toDate;
        },
        error: () => {
          const date = new Date(this.policyData.fromDate);
          date.setFullYear(date.getFullYear() + this.policyData.tenureYears);
          this.calculatedToDate = date.toISOString().split('T')[0];
        }
      });
    }
  }

  loadContent(view: string): void {
    this.currentView = view;
    this.loading = true;

    if (view === 'dashboard') {
      this.loadMyPolicies();
    } else {
      this.loading = false;
    }
  }

  loadMyPolicies(): void {
    this.apiService.get<InsurancePolicy[]>(`/underwriter/policies/${this.underwriterId}`).subscribe({
      next: (data) => {
        this.myPolicies = data;
        this.totalPremium = data.reduce((sum, p) => sum + (p.premiumAmount || 0), 0);
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  createPolicy(): void {
    if (this.policyData.phoneNo.length !== 10) {
      this.createMessage = 'Phone number must be exactly 10 digits!';
      this.createSuccess = false;
      return;
    }

    this.loading = true;
    this.policyData.fromDate = this.getTodayDate();

    this.apiService.post<any>('/underwriter/policy/create', this.policyData).subscribe({
      next: (response) => {
        if (response.status === 'success') {
          this.createMessage = response.message;
          this.createSuccess = true;
          this.resetPolicyForm();
        } else {
          this.createMessage = 'Error creating policy.';
          this.createSuccess = false;
        }
        this.loading = false;
        setTimeout(() => { this.createMessage = ''; }, 3000);
      },
      error: () => {
        this.createMessage = 'Error connecting to server.';
        this.createSuccess = false;
        this.loading = false;
      }
    });
  }

  resetPolicyForm(): void {
    this.policyData = {
      vehicleNo: '',
      vehicleType: '',
      customerName: '',
      engineNo: '',
      chassisNo: '',
      phoneNo: '',
      insuranceType: '',
      fromDate: this.getTodayDate(),
      underwriterId: this.underwriterId,
      vehicleAge: 0,
      tenureYears: 1
    };
    this.calculatedPremium = '';
    this.calculatedToDate = '';
  }

  searchForRenew(): void {
    this.loading = true;
    this.apiService.get<any>(`/underwriter/policy/${this.renewPolicyId}/${this.underwriterId}`).subscribe({
      next: (data) => {
        if (data.found) {
          this.renewDetails = data.policy;
        } else {
          alert('No such policy exists, please try again.');
          this.renewDetails = null;
        }
        this.loading = false;
      },
      error: () => {
        alert('Error searching policy');
        this.loading = false;
      }
    });
  }

  confirmRenew(): void {
    if (this.renewConfirm.toUpperCase() !== 'R') {
      alert('Invalid choice. Renewal cancelled.');
      return;
    }

    this.loading = true;
    this.apiService.put<any>('/underwriter/policy/renew', {
      policyId: this.renewPolicyId,
      underwriterId: this.underwriterId,
      newPremium: this.newPremium,
      years: this.renewYears
    }).subscribe({
      next: (response) => {
        alert(response.message);
        this.renewDetails = null;
        this.renewPolicyId = '';
        this.renewConfirm = '';
        this.loading = false;
      },
      error: () => {
        alert('Error renewing policy');
        this.loading = false;
      }
    });
  }

  searchForChange(): void {
    this.loading = true;
    this.apiService.get<any>(`/underwriter/policy/${this.changePolicyId}/${this.underwriterId}`).subscribe({
      next: (data) => {
        if (data.found) {
          if (data.policy.insuranceType === 'Third Party') {
            alert('There\'s no provision to update the policy type from Third Party to Full Insurance.');
            this.changeDetails = null;
          } else {
            this.changeDetails = data.policy;
          }
        } else {
          alert('No such policy exists, please try again.');
          this.changeDetails = null;
        }
        this.loading = false;
      },
      error: () => {
        alert('Error searching policy');
        this.loading = false;
      }
    });
  }

  confirmChange(): void {
    if (this.changeConfirm.toUpperCase() !== 'U') {
      alert('Invalid choice.');
      return;
    }

    this.loading = true;
    this.apiService.put<any>('/underwriter/policy/change-type', {
      policyId: this.changePolicyId,
      underwriterId: this.underwriterId
    }).subscribe({
      next: (response) => {
        alert(response.message);
        this.changeDetails = null;
        this.changePolicyId = '';
        this.changeConfirm = '';
        this.loading = false;
      },
      error: () => {
        alert('Error updating policy type');
        this.loading = false;
      }
    });
  }

  searchForClose(): void {
    this.loading = true;
    this.apiService.get<any>(`/underwriter/policy/${this.closePolicyId}/${this.underwriterId}`).subscribe({
      next: (data) => {
        if (data.found) {
          if (data.policy.status === 'closed') {
            alert('This policy is already closed.');
            this.closeDetails = null;
          } else {
            this.closeDetails = data.policy;
          }
        } else {
          alert('No such policy exists.');
          this.closeDetails = null;
        }
        this.loading = false;
      },
      error: () => {
        alert('Error searching policy');
        this.loading = false;
      }
    });
  }

  confirmClose(): void {
    if (this.closeConfirm.toUpperCase() !== 'C') {
      alert('Invalid choice. Policy not closed.');
      return;
    }

    this.loading = true;
    this.apiService.put<any>('/underwriter/policy/close', {
      policyId: this.closePolicyId,
      underwriterId: this.underwriterId
    }).subscribe({
      next: (response) => {
        alert(response.message);
        this.closeDetails = null;
        this.closePolicyId = '';
        this.closeConfirm = '';
        this.loading = false;
      },
      error: () => {
        alert('Error closing policy');
        this.loading = false;
      }
    });
  }

  searchForPayment(): void {
    this.loading = true;
    this.apiService.get<any>(`/underwriter/policy/${this.paymentPolicyId}/${this.underwriterId}`).subscribe({
      next: (data) => {
        if (data.found) {
          this.paymentDetails = data.policy;
        } else {
          alert('No such policy exists.');
          this.paymentDetails = null;
        }
        this.loading = false;
      },
      error: () => {
        alert('Error fetching policy details');
        this.loading = false;
      }
    });
  }

  processPayment(): void {
    if (!this.paymentId) {
      alert('Please enter payment details.');
      return;
    }

    const transactionId = `${this.paymentId.toUpperCase().slice(0, 10)}${Math.floor(Math.random() * 10000)}`;
    alert(`Payment Successful!\nAmount: ₹${this.paymentDetails?.premiumAmount}\nMethod: ${this.paymentMethod}\nTransaction ID: ${transactionId}\nPolicy ${this.paymentPolicyId} payment completed.`);
    
    this.paymentDetails = null;
    this.paymentPolicyId = '';
    this.paymentId = '';
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}