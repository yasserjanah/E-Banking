import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Customer } from 'src/app/models/customer.model';
import { AuthenticationServiceService } from 'src/app/services/authentication-service.service';
import { CustomersService } from 'src/app/services/customers.service';

@Component({
  selector: 'app-add-customer',
  templateUrl: './add-customer.component.html',
  styleUrls: ['./add-customer.component.css']
})
export class AddCustomerComponent implements OnInit {

  addNewCustomerFormGroup!: FormGroup;
  // need to use Router Guards
  isLoggedIn: boolean = false;
  userType: string | null = null;

  constructor(private customersService: CustomersService,
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
    this.addNewCustomerFormGroup = this.formBuilder.group({
      name: this.formBuilder.control("", [Validators.required]),
      email: this.formBuilder.control("", [Validators.required, Validators.email]),
      username: this.formBuilder.control("", [Validators.required, Validators.minLength(3)]),
      password: this.formBuilder.control("", [Validators.required, Validators.minLength(5)]),
      re_password: this.formBuilder.control("", [Validators.required, Validators.minLength(5)])
    });
  }

  addNewCustomer(): void {
    //let customer: Customer = this.addNewCustomerFormGroup.value;
    
    const name = this.addNewCustomerFormGroup.get('name')?.value;
    const email = this.addNewCustomerFormGroup.get('email')?.value;
    const username = this.addNewCustomerFormGroup.get('username')?.value;
    const password = this.addNewCustomerFormGroup.get('password')?.value;
    const re_password = this.addNewCustomerFormGroup.get('re_password')?.value;
    if (password != "" && password !== re_password) {
      alert("Passwords do not match!");
      return;
    }

    // check if fields are empty
    if (name === "" || email === "" || username === "") {
      alert("Please fill all the fields!");
      return;
    }
    
    this.customersService.addCustomer({name, email, username, password}).subscribe({
      next: () => {
        this.router.navigate(['/customers']);
      },
      error: (err) => {
        console.log(err);
      }
    })
  }

}
