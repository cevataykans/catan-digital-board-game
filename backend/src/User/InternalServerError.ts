import {ErrorModel} from "../Models/Error/Error";

export class InternalServerError implements ErrorModel, Error {
    code: number;
    message: string;
    name: string;
    status: number;

    constructor(){
        this.code = 1010;
        this.message = "Internal server error.";
        this.name = 'Internal Server Error';
        this.status = 404;
    }
}
