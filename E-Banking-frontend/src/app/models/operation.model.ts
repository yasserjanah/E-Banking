export interface Operation {
    id?: number;
    operationDate: string;
    amount: number;
    description: string;
    type: string;
}

export interface OperationPagination {
    accountId: string;
    balance: number;
    currentPage: number;
    totalPages: number;
    pageSize: number;
    accountOperationDTOS: Operation[];
}