import {ErrorModel} from "../Models/Error/Error";

export class InvalidUserInformation implements ErrorModel, Error {
    code: number;
    message: string;
    name: string;
    status: number;

    constructor(){
        this.code = 1003;
        this.message = "Invalid information";
        this.name = 'Invalid Information';
        this.status = 400;
    }
}
