import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Account } from 'src/app/models/account.model';
import { Operation, OperationPagination } from 'src/app/models/operation.model';
import { AccountsService } from 'src/app/services/accounts.service';
import { AuthenticationServiceService } from 'src/app/services/authentication-service.service';

@Component({
  selector: 'app-account-detail',
  templateUrl: './account-detail.component.html',
  styleUrls: ['./account-detail.component.css']
})
export class AccountDetailComponent implements OnInit {

  account?: Account;
  operations?: Operation[];
  operationsPagination?: OperationPagination
  currentPage: number = 0;
  currentSize: number = 10;
  totalPages: Array<number> = [];

  // router varialbles
  routerAccountId?: string;
  // need to use Router Guards
  isLoggedIn: boolean = false;
  userType: string | null = null;

  constructor(private accountsService: AccountsService,
              private authService: AuthenticationServiceService,
              private router: Router) { }

  ngOnInit(): void {
    this.isLoggedIn =  this.authService.isLoggedIn
    this.userType = this.authService.userType;
    if (!this.isLoggedIn){
      this.router.navigate(['/login']);
    }

    this.routerAccountId = this.router.url.split('/')[this.router.url.split('/').length - 1];
    this.getAccount(this.routerAccountId);
    // this.getAccountOperations(this.router.url.split('/')[2]);
    this.getAccountOperationsPaginated(this.routerAccountId, this.currentPage, this.currentSize);
  }

  getAccount(account_id: string | undefined): void {
    this.accountsService.getOneAccount(account_id).subscribe({
      next: (account) => {
        console.log(account);
        this.account = account;
      },
      error: (err) => {
        console.log(err);
      }
    });
  }

  getAccountOperations(account_id: string | undefined): void {
    this.accountsService.getAccountOperations(account_id).subscribe({
      next: (operations) => {
        console.log(operations);
        this.operations = operations;
      },
      error: (err) => {
        console.log(err);
      }
    });
  }

  getAccountOperationsPaginated(account_id: string | undefined, page: number, size: number): void {
    this.accountsService.getAccountOperationsPaginated(account_id, page, size).subscribe({
      next: (ops) => {
        this.currentPage = ops.currentPage;
        this.currentSize = ops.pageSize;
        this.totalPages = Array.from(Array(ops.totalPages).keys())
        this.operationsPagination = ops;
        console.log(this.operationsPagination);
      },
      error: (err) => {
        console.log(err);
      }
    });
  }

  goToPage(page: number): void {
    this.currentPage = page;
    this.getAccountOperationsPaginated(this.routerAccountId, page, this.currentSize);
  }

}
