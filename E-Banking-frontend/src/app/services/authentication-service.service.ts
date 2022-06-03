import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { BehaviorSubject, map, Observable } from 'rxjs';
import { API_CURRENT_USER_URL, API_LOGIN_URL, API_REFRESH_TOKEN_URL } from 'src/environments/environment';
import { AppUser } from '../models/appuser.model';

@Injectable({
    providedIn: 'root'
})
export class AuthenticationServiceService {

    accessToken: string | null = null;
    refreshToken: string | null = null;
    userType: string | null = null;
    isLoggedIn: boolean =  false;


    constructor(private httpClient: HttpClient, private router: Router) {
        let appUser = localStorage.getItem('currentAppUser');
        if (appUser) {
            this.accessToken = localStorage.getItem('accessToken');
            this.refreshToken = localStorage.getItem('refreshToken');
            this.userType = localStorage.getItem('userType');
            this.isLoggedIn = true;
        } else {
            this.router.navigate(['/login']);
        }
    }


    login(username: any, password: any){
        let headers = new HttpHeaders();
        headers.append('Content-Type', 'application/json');
        return this.httpClient.post<any>(API_LOGIN_URL, { username, password }, { headers })
        .subscribe(
            (tokens) => {
                console.log(tokens);
                this.accessToken = tokens.accessToken;
                this.refreshToken = tokens.refreshToken;
                localStorage.setItem('accessToken', tokens.accessToken);
                localStorage.setItem('refreshToken', tokens.refreshToken);
                this.getCurrentUser();
            },
            (error) => {
                console.log(error);
            }
        );
    }

    refreshTokenRequest() {
        let headers = new HttpHeaders();
        headers = headers.append('Content-Type', 'application/json');
        headers = headers.append('Authorization', `Bearer ${this.refreshToken}`);
        return this.httpClient.get<any>(API_REFRESH_TOKEN_URL,{ headers })
        .subscribe(
            (tokens) => {
                console.log(tokens);
                this.accessToken = tokens.accessToken;
                this.refreshToken = tokens.refreshToken;
                localStorage.setItem('accessToken', tokens.accessToken);
                localStorage.setItem('refreshToken', tokens.refreshToken);
                this.getCurrentUser();
            },
            (error) => {
                console.log(error);
            }
        );
    }

    getCurrentUser() {
        let headers = new HttpHeaders();
        // token authorization
        headers = headers.set('Authorization', `Bearer ${this.accessToken}`);
        headers = headers.set('Content-Type', 'application/json');
        this.httpClient.get<AppUser>(API_CURRENT_USER_URL, { headers })
        .subscribe(
            (appUser) => {
                console.log(appUser);
                localStorage.setItem('currentAppUser', JSON.stringify(appUser));
                localStorage.setItem('userType', appUser.userType);
                this.userType = appUser.userType;
                localStorage.setItem('isLoggedIn', 'true');
                this.isLoggedIn = true;
                // reload the page
                if (this.userType === 'EMPLOYEE') {
                    this.router.navigate(['/']);
                } else if (this.userType === 'CUSTOMER') {
                    this.router.navigate(['/portal']);
                }
            },
            (error) => {
                console.log("Auth error: ",error);
            }
        );
    }

    logout() {
        console.log('logout');
        this.accessToken = null;
        this.refreshToken = null;
        this.userType = null;
        this.isLoggedIn = false;
        // remove user from local storage and set current user to null
        localStorage.removeItem('currentAppUser');
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        localStorage.removeItem('userType');
        localStorage.removeItem('isLoggedIn');
    }
}
