import { HttpHeaders } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Router } from '@angular/router';
import { catchError, map, Observable, throwError } from 'rxjs';
import { Customer } from 'src/app/models/customer.model';
import { AuthenticationServiceService } from 'src/app/services/authentication-service.service';
import { CustomersService } from 'src/app/services/customers.service';


@Component({
  selector: 'app-customers',
  templateUrl: './customers.component.html',
  styleUrls: ['./customers.component.css']
})
export class CustomersComponent implements OnInit {

  customers: Observable<Customer[]> | undefined;
  errorMessage: string | undefined;
  searchFormGroup!: FormGroup;
  // need to setup router Guard
  isLoggedIn: boolean = false;
  userType: string | null = null;

  constructor(private customersService: CustomersService,
              private authService: AuthenticationServiceService,
              private formBuilder: FormBuilder,
              private router: Router) { }

  ngOnInit(): void {
    //
    console.log('customers component init');
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
    this.searchFormGroup = this.formBuilder.group({
      name: this.formBuilder.control(""),
    });
    // get all customers
    this.searchCustomers();
  }

  searchCustomers(): void {
    const name = this.searchFormGroup.get('name')?.value;
    this.customers = this.customersService.searchCustomers(name).pipe(
      catchError(err => {
        if (err.status === 401) {
          this.authService.refreshTokenRequest()
          console.log('refresh token request');
        }
        this.errorMessage = err.message;
        return throwError(() => err);
      })
    );
  }

  deleteCustomer(customer_id: string | undefined): void {
    this.customersService.deleteCustomer(customer_id).subscribe({
      next: () => {
        // remove the customer from the list
        this.customers = this.customers?.pipe(
          map(customers => customers.filter(customer => customer.userId !== customer_id))
        );
      },
      error: (err) => {
        console.log(err);
      }
    });
  }

  goToCustomerAccounts(customer_id: string | undefined): void {
    this.router.navigate(['/customers', customer_id]);
  }

}
