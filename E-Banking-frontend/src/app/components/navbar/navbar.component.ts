import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthenticationServiceService } from 'src/app/services/authentication-service.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {

  isLoggedIn: boolean = false;
  userType: string | null = null;

  // inject the router into the constructor
  constructor(private router: Router,
              private authService: AuthenticationServiceService) {
  }

  ngOnInit(): void {
    // get the current user from the local storage
    this.isLoggedIn =  this.authService.isLoggedIn
    this.userType = this.authService.userType;
  }

  isRouteActive(route: string): boolean {
    return this.router.url === route;
  }

  login() {
    console.log('login');
    this.router.navigate(['/login']);
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

}
