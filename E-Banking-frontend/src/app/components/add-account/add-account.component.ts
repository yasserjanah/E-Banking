import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Account } from 'src/app/models/account.model';
import { Customer } from 'src/app/models/customer.model';
import { AccountsService } from 'src/app/services/accounts.service';
import { AuthenticationServiceService } from 'src/app/services/authentication-service.service';
import { CustomersService } from 'src/app/services/customers.service';

@Component({
  selector: 'app-add-account',
  templateUrl: './add-account.component.html',
  styleUrls: ['./add-account.component.css']
})
export class AddAccountComponent implements OnInit {

  is_current_or_saving?: string = undefined;
  overDraftIsNotSet: boolean = true;
  interestRateIsNotSet: boolean = true;
  customerIsNotExists: boolean = false;
  resultCustomers?: Customer[];
  selectedCustomer?: Customer;
  addNewAccountFormGroup!: FormGroup;

  // need to use Router Guards
  isLoggedIn: boolean = false;
  userType: string | null = null;

  constructor(private accountsService: AccountsService,
              private customersService: CustomersService,
              private formBuilder: FormBuilder,
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

    // initialize the form
    this.addNewAccountFormGroup = this.formBuilder.group({
      accountType: this.formBuilder.control("", [Validators.required]),
      balance: this.formBuilder.control("", [Validators.required]),
      status: this.formBuilder.control("", [Validators.required]),
      createdAt: this.formBuilder.control("", [Validators.required]),
      interestRate: this.formBuilder.control(""),
      overDraft: this.formBuilder.control(""),
      customer: this.formBuilder.control("")
    });
  }

  addNewAccount(): void {
    let account: Account = {
      accountType: '',
      balance: 0
    }
    account.accountType = this.addNewAccountFormGroup.get("accountType")?.value;
    account.balance = this.addNewAccountFormGroup.get("balance")?.value;
    account.status = this.addNewAccountFormGroup.get("status")?.value;
    account.createdAt = this.addNewAccountFormGroup.get("createdAt")?.value;
    if (account.accountType == "SavingAccount" && this.addNewAccountFormGroup.get("interestRate")?.value) {
      account.interestRate = this.addNewAccountFormGroup.get("interestRate")?.value;
    }
    if (account.accountType == "CurrentAccount" && this.addNewAccountFormGroup.get("overDraft")?.value) {
      account.overDraft = this.addNewAccountFormGroup.get("overDraft")?.value;
    }
    if (this.addNewAccountFormGroup.get("customer")?.value) {
      account.customerDTO = this.selectedCustomer;
    }

    console.log(account);
    this.accountsService.addAccount(account).subscribe(
      (result) => {
        console.log(result);
        this.router.navigate(['/accounts']);
      },
      (error) => {
        console.log(error);
      }
    );
  }

  OnChangeAccountType(event: any): void {
    this.is_current_or_saving = event.target.value;
  }

  updateinterestRateIsNotSet(event: any): void {
    if (parseFloat(event.target.value) < 0) {
      this.interestRateIsNotSet = true;
    } else {
      this.interestRateIsNotSet = false;
    }
  }

  updateoverDraftIsNotSet(event: any): void {
    if (parseFloat(event.target.value) < 0) {
      this.overDraftIsNotSet = true;
    } else {
      this.overDraftIsNotSet = false;
    }
  }

  searchCustomer(event: any): void {
    let keyword = event.target.value != "" ? event.target.value: "<NO_NEED_FOR_DATA_TO_BE_RETURNED>"
    this.customersService.searchCustomers(keyword).subscribe(
      (result) => {
        this.resultCustomers = result;
      },
      (error) => {
        console.log(error);
      }
    );
  }

  updateSelectedCustomer(c: Customer): void {
    this.addNewAccountFormGroup.get("customer")?.setValue(c.name);
    this.selectedCustomer = c;
    this.customerIsNotExists = false;
  }

}
