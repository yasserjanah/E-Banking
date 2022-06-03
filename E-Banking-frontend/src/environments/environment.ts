// This file can be replaced during build by using the `fileReplacements` array.
// `ng build` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

export const environment = {
  production: false,
};

export const API_BASE_URL: string = 'http://localhost:8080';

export const API_LOGIN_URL: string = `${API_BASE_URL}/login`;
export const API_CURRENT_USER_URL: string = `${API_BASE_URL}/users/current`;
export const API_REFRESH_TOKEN_URL: string = `${API_BASE_URL}/users/refresh-token`;

export const API_CUSTOMERS_URL: string = `${API_BASE_URL}/customers`;
export const API_CUSTOMERS_SEARCH_URL: string = `${API_BASE_URL}/customers/search`;

export const API_ACCOUNTS_URL: string = `${API_BASE_URL}/accounts`;
export const API_OPERATION_DEBIT_URL: string = `${API_BASE_URL}/accounts/debit`;
export const API_OPERATION_CREDIT_URL: string = `${API_BASE_URL}/accounts/credit`;
export const API_OPERATION_TRANSFER_URL: string = `${API_BASE_URL}/accounts/transfer`;

/*
 * For easier debugging in development mode, you can import the following file
 * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
 *
 * This import should be commented out in production mode because it will have a negative impact
 * on performance if an error is thrown.
 */
// import 'zone.js/plugins/zone-error';  // Included with Angular CLI.
