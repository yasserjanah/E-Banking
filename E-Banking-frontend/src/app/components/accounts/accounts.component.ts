import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, map, Observable, throwError } from 'rxjs';
import { Account } from 'src/app/models/account.model';
import { AccountsService } from 'src/app/services/accounts.service';
import { AuthenticationServiceService } from 'src/app/services/authentication-service.service';

@Component({
  selector: 'app-accounts',
  templateUrl: './accounts.component.html',
  styleUrls: ['./accounts.component.css']
})
export class AccountsComponent implements OnInit {


  accounts?: Observable<Account[]>;
  errorMessage: string | undefined;
  // need to setup router Guard
  isLoggedIn: boolean = false;
  userType: string | null = null;

  constructor(private accountsService: AccountsService,
              private authService: AuthenticationServiceService,
              private router: Router) { }

  ngOnInit(): void {
    console.log('accounts component init');
    this.isLoggedIn =  this.authService.isLoggedIn
    this.userType = this.authService.userType;
    if (!this.isLoggedIn){
      this.router.navigate(['/login']);
    }
    // go back if userType is not EMPLOYEE
    if (this.userType !== 'EMPLOYEE') {
      this.router.navigate(['/']);
    }
    this.getAllAccounts();
  }

  getAllAccounts(): void {
    this.accounts = this.accountsService.getAllAccounts().pipe(
      catchError(err => {
        console.log(err);
        this.errorMessage = err.message;
        return throwError(() => err);
      })
    );
  }

  goToAccountDetail(account_id: string | undefined): void {
    this.router.navigate(['/accounts', account_id]);
  }


}
