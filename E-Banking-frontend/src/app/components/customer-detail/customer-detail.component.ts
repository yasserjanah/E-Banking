import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Account } from 'src/app/models/account.model';
import { Customer } from 'src/app/models/customer.model';
import { AuthenticationServiceService } from 'src/app/services/authentication-service.service';
import { CustomersService } from 'src/app/services/customers.service';

@Component({
  selector: 'app-customer-detail',
  templateUrl: './customer-detail.component.html',
  styleUrls: ['./customer-detail.component.css']
})
export class CustomerDetailComponent implements OnInit {

  customer?: Customer;
  bankAccounts?: Account[];
  // need to use Router Guards
  isLoggedIn: boolean = false;
  userType: string | null = null;

  constructor(private customersService: CustomersService,
              private authService: AuthenticationServiceService,
              private router: Router) { }

  ngOnInit(): void {
    //
    this.isLoggedIn =  this.authService.isLoggedIn
    this.userType = this.authService.userType;
    if (!this.isLoggedIn){
      this.router.navigate(['/login']);
    }
    // go back if userType is not EMPLOYEE
    if (this.userType !== 'EMPLOYEE') {
      this.router.navigate(['/']);
    }
    // get the customer id from the route
    this.getCustomer(this.router.url.split('/')[2]);
    this.getCustomerBankAccounts(this.router.url.split('/')[2]);
  }

  getCustomer(customer_id: string | undefined): void {
    // get the customer from the service
    this.customersService.getOneCustomer(customer_id).subscribe({
      next: (customer) => {
        this.customer = customer;
      },
      error: (err) => {
        console.log(err);
      }
    });
  }

  getCustomerBankAccounts(customer_id: string | undefined): void {
    this.customersService.getCustomerBankAccounts(customer_id).subscribe({
      next: (accounts) => {
        this.bankAccounts = accounts;
      },
      error: (err) => {
        console.log(err);
      }
    });
  }

  goToAccountDetail(account_id: string | undefined): void {
    this.router.navigate(['/accounts', account_id]);
  }

}
