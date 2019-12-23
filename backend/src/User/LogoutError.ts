import {ErrorModel} from "../Models/Error/Error";

export class LogoutError implements ErrorModel , Error{
    code: number;
    message: string;
    name: string;
    status: number;

    constructor(){
        this.code = 1012;
        this.message = "Error has occured while trying to logout!";
        this.name = 'Logout Error';
        this.status = 400;
    }


}
