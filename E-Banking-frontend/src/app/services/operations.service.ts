import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { API_OPERATION_CREDIT_URL, API_OPERATION_DEBIT_URL, API_OPERATION_TRANSFER_URL } from 'src/environments/environment';
import { AuthenticationServiceService } from './authentication-service.service';

@Injectable({
  providedIn: 'root'
})
export class OperationsService {

  headers?: HttpHeaders;

  constructor(private httpClient: HttpClient,
              private authService: AuthenticationServiceService) {
      this.headers = new HttpHeaders();
      this.headers = this.headers.set('Authorization', `Bearer ${this.authService.accessToken}`);
      this.headers = this.headers.set('Content-Type', 'application/json');
  }

  createDebitOperation(operation: any) {
    return this.httpClient.post(API_OPERATION_DEBIT_URL, operation, { headers: this.headers });
  }

  createCreditOperation(operation: any) {
    return this.httpClient.post(API_OPERATION_CREDIT_URL, operation, { headers: this.headers });
  }

  createTransferOperation(operation: any) {
    return this.httpClient.post(API_OPERATION_TRANSFER_URL, operation, { headers: this.headers });
  }

}
