import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AccountDetailComponent } from './components/account-detail/account-detail.component';
import { AccountsComponent } from './components/accounts/accounts.component';
import { AddAccountComponent } from './components/add-account/add-account.component';
import { AddCustomerComponent } from './components/add-customer/add-customer.component';
import { AddOperationComponent } from './components/add-operation/add-operation.component';
import { CustomerDetailComponent } from './components/customer-detail/customer-detail.component';
import { CustomersComponent } from './components/customers/customers.component';
import { HomeComponent } from './components/home/home.component';
import { LoginComponent } from './components/login/login.component';
import { MyCustomerComponent } from './components/my-customer/my-customer.component';

const routes: Routes = [
  // login route
  { path: 'login', component: LoginComponent },
  // home route
  { path: '', component: HomeComponent },
  // customers route
  { path: 'portal', component: MyCustomerComponent },
  { path: 'portal/account/:id', component: AccountDetailComponent },
  { path: 'portal/:customer_id/operations/new', component: AddOperationComponent },
  // admin routes
  { path: 'customers', component: CustomersComponent },
  { path: 'customers/new', component: AddCustomerComponent },
  { path: 'customers/:id', component: CustomerDetailComponent },
  { path: 'accounts', component: AccountsComponent },
  { path: 'accounts/new', component: AddAccountComponent },
  { path: 'accounts/:id', component: AccountDetailComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {onSameUrlNavigation: 'reload'})],
  exports: [RouterModule]
})
export class AppRoutingModule { }
