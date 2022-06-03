import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_ACCOUNTS_URL } from 'src/environments/environment';
import { Account } from '../models/account.model';
import { Operation, OperationPagination } from '../models/operation.model';
import { AuthenticationServiceService } from './authentication-service.service';

@Injectable({
  providedIn: 'root'
})
export class AccountsService {
  
  headers?: HttpHeaders;

  constructor(private httpClient: HttpClient,
              private authService: AuthenticationServiceService) {
    this.headers = new HttpHeaders();
    this.headers = this.headers.set('Authorization', `Bearer ${this.authService.accessToken}`);
    this.headers = this.headers.set('Content-Type', 'application/json');
  }

  getAllAccounts(): Observable<Account[]> {
    return this.httpClient.get<Account[]>(API_ACCOUNTS_URL, { headers: this.headers });
  }

  getOneAccount(id?: string): Observable<Account> {
    return this.httpClient.get<Account>(API_ACCOUNTS_URL + '/' + id, { headers: this.headers });
  }

  getAccountOperations(id?: string): Observable<Operation[]> {
    return this.httpClient.get<Operation[]>(API_ACCOUNTS_URL + '/' + id + '/operations', { headers: this.headers });
  }

  getAccountOperationsPaginated(id?: string, page?: number, size?: number): Observable<OperationPagination> {
    return this.httpClient.get<OperationPagination>(API_ACCOUNTS_URL + '/' + id + '/pageOperations?page=' + page + '&size=' + size, { headers: this.headers });
  }

  deleteAccount(id?: string): Observable<Account> {
    return this.httpClient.delete<Account>(API_ACCOUNTS_URL + '/' + id, { headers: this.headers });
  }

  addAccount(account: Account): Observable<Account> {
    return this.httpClient.post<Account>(API_ACCOUNTS_URL, account, { headers: this.headers });
  }

}
