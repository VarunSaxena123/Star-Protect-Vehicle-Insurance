export interface InsurancePolicy {
  policyId: string;
  vehicleNo: string;
  vehicleType: string;
  customerName: string;
  engineNo: string;
  chassisNo: string;
  phoneNo: string;
  premiumAmount: number;
  insuranceType: string;
  fromDate: string;
  toDate: string;
  underwriterId: string;
  status: string;
  vehicleAge: number;
  createdDate?: string;
  renewedFrom?: string;
}

export interface CreatePolicyRequest {
  vehicleNo: string;
  vehicleType: string;
  customerName: string;
  engineNo: string;
  chassisNo: string;
  phoneNo: string;
  insuranceType: string;
  fromDate: string;
  underwriterId: string;
  vehicleAge: number;
  tenureYears: number;
}

export interface RenewPolicyRequest {
  policyId: string;
  underwriterId: string;
  newPremium: number;
  years: number;
}

export interface ChangeTypeRequest {
  policyId: string;
  underwriterId: string;
}

export interface ClosePolicyRequest {
  policyId: string;
  underwriterId: string;
}

export interface PremiumCalculationRequest {
  vehicleType: string;
  insuranceType: string;
  vehicleAge: number;
}

export interface ToDateCalculationRequest {
  fromDate: string;
  years: number;
}

export interface DashboardStats {
  totalUnderwriters?: number;
  totalPolicies?: number;
  activePolicies?: number;
  pendingPolicies?: number;
  expiredPolicies?: number;
  totalPremium?: number;
}

export interface ApiResponse {
  status?: string;
  message?: string;
  success?: boolean;
  found?: boolean;
  underwriterId?: string;
  policy?: InsurancePolicy;
  policies?: InsurancePolicy[];
  isExpired?: boolean;
  premiumAmount?: number;
  toDate?: string;
}