import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-landing',
  templateUrl: './landing.component.html',
  styleUrls: ['./landing.component.css']
})
export class LandingComponent {
  constructor(private router: Router) {}

  navigateToLogin(): void {
    this.router.navigate(['/login']);
  }

  // Remove the parameter - don't pass $event
  estimatePremium(): void {
    const vehicleInput = document.getElementById('quoteVehicle') as HTMLInputElement;
    const quoteType = document.getElementById('quoteType') as HTMLSelectElement;
    const quoteMobile = document.getElementById('quoteMobile') as HTMLInputElement;
    const quotePreview = document.getElementById('quotePreview');
    
    const vehicle = vehicleInput?.value.trim();
    const type = quoteType?.value;
    const mobile = quoteMobile?.value.trim();
    
    if (!vehicle) {
      if (quotePreview) {
        quotePreview.innerHTML = '<span class="text-danger"><i class="fas fa-exclamation-circle"></i> Please enter vehicle number</span>';
      }
      return;
    }
    
    if (mobile && mobile.length !== 10) {
      if (quotePreview) {
        quotePreview.innerHTML = '<span class="text-danger">Mobile number must be 10 digits</span>';
      }
      return;
    }
    
    let basePremium = type === '4-wheeler' ? 6500 : 2500;
    const randomAddon = Math.floor(Math.random() * 1500);
    const total = basePremium + randomAddon;
    
    if (quotePreview) {
      quotePreview.innerHTML = `<div class="alert alert-success mt-2 py-2"><i class="fas fa-check-circle"></i> Estimated Premium: <strong>₹${total.toLocaleString()}</strong> for ${type === '4-wheeler' ? '4-Wheeler' : '2-Wheeler'}. <br><small>Login to purchase instantly.</small></div>`;
      
      setTimeout(() => {
        if (quotePreview.innerHTML.includes('Estimated')) {
          quotePreview.innerHTML = '';
        }
      }, 8000);
    }
  }

  // Fix the parameter type to use Event
  validateMobile(event: Event): void {
    const input = event.target as HTMLInputElement;
    input.value = input.value.replace(/[^0-9]/g, '').slice(0, 10);
  }
}