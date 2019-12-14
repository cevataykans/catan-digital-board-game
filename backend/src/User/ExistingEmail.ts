import {ErrorModel} from "../Models/Error/Error";

export class ExistingEmail implements ErrorModel , Error{
    code: number;
    message: string;
    name: string;
    status: number;

    constructor(){
        this.code = 1000;
        this.message = "Email is already in use";
        this.name = 'Existing Email';
        this.status = 400;
    }


}
