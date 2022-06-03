import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Account } from 'src/app/models/account.model';
import { Customer } from 'src/app/models/customer.model';
import { AccountsService } from 'src/app/services/accounts.service';
import { AuthenticationServiceService } from 'src/app/services/authentication-service.service';
import { CustomersService } from 'src/app/services/customers.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  accountsCount?: number;
  custumersCount?: number;
  // need to setup router Guard
  isLoggedIn: boolean = false;
  userType: string | null = null;

  constructor(
    private router: Router,
    private accountsService: AccountsService,
    private customersService: CustomersService,
    private authService: AuthenticationServiceService
  ) { }

  ngOnInit(): void {
    console.log('home component init');
    this.isLoggedIn =  this.authService.isLoggedIn
    this.userType = this.authService.userType;
    if (!this.isLoggedIn){
      this.router.navigate(['/login']);
    }
    // go back if userType is not EMPLOYEE
    if (this.userType !== 'EMPLOYEE') {
      this.router.navigate(['/portal']);
    }
    this.getAccounts();
    this.getCustomers();
  }

  getAccounts(): void {
    this.accountsService.getAllAccounts().subscribe(
      accounts => {
        this.accountsCount = accounts.length;
      }
    );
  }

  getCustomers(): void {
    this.customersService.getCustomers().subscribe(
      customers => {
        this.custumersCount = customers.length;
      }
    );
  }


}
