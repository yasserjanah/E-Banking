import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Account } from 'src/app/models/account.model';
import { AppUser } from 'src/app/models/appuser.model';
import { Customer } from 'src/app/models/customer.model';
import { Operation, OperationPagination } from 'src/app/models/operation.model';
import { AccountsService } from 'src/app/services/accounts.service';
import { AuthenticationServiceService } from 'src/app/services/authentication-service.service';
import { CustomersService } from 'src/app/services/customers.service';

@Component({
  selector: 'app-my-customer',
  templateUrl: './my-customer.component.html',
  styleUrls: ['./my-customer.component.css']
})
export class MyCustomerComponent implements OnInit {

  customer?: Customer;
  bankAccounts?: Account[];
  operationsPagination?: OperationPagination;
  operations?: Operation[];
  PITab: boolean = true;
  ACTab: boolean = false;
  LTTab: boolean = false;
  // need to use Router Guards
  isLoggedIn: boolean = false;
  userType: string | null = null;
  appUser: string | null = null;

  constructor(
        private customersService: CustomersService,
        private accountsService: AccountsService,
        private router: Router,
        private authService: AuthenticationServiceService
  ) { }

  ngOnInit(): void {
    this.isLoggedIn =  this.authService.isLoggedIn
    this.userType = this.authService.userType;
    if (!this.isLoggedIn){
      this.router.navigate(['/login']);
    }
    // go back if userType is not EMPLOYEE
    if (this.userType !== 'CUSTOMER') {
      this.router.navigate(['/']);
    }
    this.appUser = localStorage.getItem('currentAppUser');
    let userId = JSON.parse(this.appUser!).userId;

    this.customersService.getCustomers().subscribe(
      (data) => {
        this.customer = data.filter(customer => customer.userId === userId)[0];
        this.getCustomerBankAccounts(this.customer?.userId);
      },
      (error) => {
        console.log(error);
      }
    );
  }

  getCustomerBankAccounts(customer_id: string | undefined): void {
    this.customersService.getCustomerBankAccounts(customer_id).subscribe({
      next: (accounts) => {
        console.log(accounts);
        // for now we only show the first 5 operations from the first account
        // TODO: create lastTransactions on the back-end
        this.getAccountOperations(accounts[0].id);
        this.bankAccounts = accounts;
      },
      error: (err) => {
        console.log(err);
      }
    });
  }

  goToAccountDetail(account_id: string | undefined): void {
    this.router.navigate(['/portal/account/', account_id]);
  }

  getAccountOperations(account_id: string | undefined): void {
    this.accountsService.getAccountOperations(account_id).subscribe({
      next: (operations) => {
        console.log(operations);
        this.operations = operations.slice(0, 5);
      },
      error: (err) => {
        console.log(err);
      }
    });
  }

  updateTab(clicked_tab: string){
    switch(clicked_tab){
      case 'PITab':
        this.PITab = true;
        this.ACTab = false;
        this.LTTab = false;
        break;
      case 'ACTab':
        this.PITab = false;
        this.ACTab = true;
        this.LTTab = false;
        break;
      case 'LTTab':
        this.PITab = false;
        this.ACTab = false;
        this.LTTab = true;
        break;
    }
  }

  goToAddNewOperation(): void {
    this.router.navigate(['portal/'+this.customer?.userId+'/operations/new']);
  }


}
