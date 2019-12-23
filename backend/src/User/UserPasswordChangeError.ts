import {ErrorModel} from "../Models/Error/Error";

export class UserPasswordChangeError implements ErrorModel, Error{
    code: number;
    message: string;
    name: string;
    status: number;

    constructor(){
        this.code = 1004;
        this.message = "User password change error";
        this.name = 'Password Change Error';
        this.status = 304;
    }
}
