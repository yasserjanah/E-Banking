import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Account } from 'src/app/models/account.model';
import { AccountsService } from 'src/app/services/accounts.service';
import { AuthenticationServiceService } from 'src/app/services/authentication-service.service';
import { CustomersService } from 'src/app/services/customers.service';
import { OperationsService } from 'src/app/services/operations.service';

@Component({
  selector: 'app-add-operation',
  templateUrl: './add-operation.component.html',
  styleUrls: ['./add-operation.component.css']
})
export class AddOperationComponent implements OnInit {

  operationType: string = "DEBIT"; // can have three values: CREDIT, DEBIT, TRANSFER
  creditOperationFormGroup!: FormGroup;
  debitOperationFormGroup!: FormGroup;
  transferOperationFormGroup!: FormGroup;
  accounts?: Account[];
  allAccounts?: Account[];
  // need to use Router Guards
  isLoggedIn: boolean = false;
  userType: string | null = null;

  constructor(
        private accountsService: AccountsService,
        private operationsService: OperationsService,
        private customersService: CustomersService,
        private authService: AuthenticationServiceService,
        private formBuilder: FormBuilder,
        private router: Router
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
      this.loadCustomerAccounts();
       // initialize the forms
      this.creditOperationFormGroup = this.formBuilder.group({
        accountId: this.formBuilder.control("", [Validators.required]),
        amount: this.formBuilder.control(0, [Validators.required, Validators.min(0)]),
        description: this.formBuilder.control("", [Validators.required]),
      });

      this.debitOperationFormGroup = this.formBuilder.group({
        accountId: this.formBuilder.control("", [Validators.required]),
        amount: this.formBuilder.control(0, [Validators.required, Validators.min(0)]),
        description: this.formBuilder.control("", [Validators.required]),
      });

      this.transferOperationFormGroup = this.formBuilder.group({
        fromAccountId: this.formBuilder.control("", [Validators.required]),
        toAccountId: this.formBuilder.control("", [Validators.required]),
        amount: this.formBuilder.control(0, [Validators.required, Validators.min(0)]),
        description: this.formBuilder.control("", [Validators.required]),
      });
  }

  loadCustomerAccounts(): void {
    this.customersService.getCustomerBankAccounts(this.router.url.split('/')[2])
      .subscribe({
        next: (accounts) => {
          this.accounts = accounts;
          this.loadAllAccounts(this.accounts);
        },
        error: (err) => {
          console.log(err);
        }
    });
  }

  loadAllAccounts(accounts_to_be_removed: Account[]): void {
    this.accountsService.getAllAccounts().subscribe({
      next: (accounts) => {
        this.allAccounts = accounts;
        // accounts_to_be_removed.forEach(account => {
        //   this.allAccounts = this.allAccounts?.filter(acc => acc.id != account.id);
        // });
      },
      error: (err) => {
        console.log(err);
      }
    });
  }

  createNewDEBITOperation(): void {
    console.log(this.debitOperationFormGroup.value);
    this.operationsService.createDebitOperation(this.debitOperationFormGroup.value)
      .subscribe({
        next: (operation) => {
          this.router.navigate(['/portal'])
        }
      });
  }

  createNewCREDITOperation(): void {
    console.log(this.creditOperationFormGroup.value);
    this.operationsService.createCreditOperation(this.creditOperationFormGroup.value)
      .subscribe({
        next: (operation) => {
          this.router.navigate(['/portal'])
        }
      });
  }

  createNewTRANSFEROperation(): void {
    console.log(this.transferOperationFormGroup.value);
    this.operationsService.createTransferOperation(this.transferOperationFormGroup.value)
      .subscribe({
        next: (operation) => {
          this.router.navigate(['/portal'])
        }
      });
  }

  updateOperationType(operationType: string): void {
    this.operationType = operationType;
  }

}
