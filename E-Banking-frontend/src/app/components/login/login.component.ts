import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthenticationServiceService } from 'src/app/services/authentication-service.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  loginForm!: FormGroup;
  // need to use Router Guards
  isLoggedIn: boolean = false;
  userType: string | null = null;

  constructor(private authService: AuthenticationServiceService,
              private router: Router,
              private formBuilder: FormBuilder) { }

  ngOnInit(): void {
    this.isLoggedIn =  this.authService.isLoggedIn
    this.userType = this.authService.userType;
    if (this.isLoggedIn){
      this.router.navigate(['/']);
    }
    // initialize the form
    this.loginForm = this.formBuilder.group({
      username: this.formBuilder.control("", [Validators.required, Validators.minLength(3)]),
      password: this.formBuilder.control("", [Validators.required, Validators.minLength(5)])
    });
  }

  login(): void {
    // console.log(this.loginForm.value);
    // call the login method in the auth service
    this.authService.login(this.loginForm.value.username, this.loginForm.value.password)
    // reload the page
  }

}
