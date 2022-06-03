import { Customer } from "./customer.model";

export interface Account {
    id?: string;
    accountType: string;
    balance: number;
    status?: string;
    createdAt?: string;
    interestRate?: number;
    overDraft?: number;
    customerDTO?: Customer;
}