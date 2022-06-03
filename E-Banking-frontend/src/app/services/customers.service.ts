import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_CUSTOMERS_SEARCH_URL, API_CUSTOMERS_URL } from 'src/environments/environment';
import { Account } from '../models/account.model';
import { Customer } from '../models/customer.model';
import { AuthenticationServiceService } from './authentication-service.service';

@Injectable({
  providedIn: 'root'
})
export class CustomersService {

  headers?: HttpHeaders;

  constructor(private httpClient: HttpClient,
              private authService: AuthenticationServiceService) {
    this.headers = new HttpHeaders();
    this.headers = this.headers.set('Authorization', `Bearer ${this.authService.accessToken}`);
    this.headers = this.headers.set('Content-Type', 'application/json');
  }

  getCustomers(): Observable<Customer[]> {
    return this.httpClient.get<Customer[]>(API_CUSTOMERS_URL, { headers: this.headers });
  }

  searchCustomers(name: string): Observable<Customer[]> {
    return this.httpClient.get<Customer[]>(API_CUSTOMERS_SEARCH_URL + '?name=' + name, { headers: this.headers });
  }

  addCustomer(obj: unknown): Observable<Customer> {
    return this.httpClient.post<Customer>(API_CUSTOMERS_URL, obj, { headers: this.headers });
  }

  deleteCustomer(customer_id?: string): Observable<Customer> {
    return this.httpClient.delete<Customer>(API_CUSTOMERS_URL + '/' + customer_id, { headers: this.headers });
  }

  getOneCustomer(customer_id?: string): Observable<Customer> {
    return this.httpClient.get<Customer>(API_CUSTOMERS_URL + '/' + customer_id, { headers: this.headers });
  }

  getCustomerBankAccounts(customer_id?: string): Observable<Account[]> {
    return this.httpClient.get<Account[]>(API_CUSTOMERS_URL + '/' + customer_id + '/accounts', { headers: this.headers });
  }
  
}
