import {ErrorModel} from "../Models/Error/Error";

export class AlreadySignedInError implements ErrorModel, Error {
    code: number;
    message: string;
    name: string;
    status: number;

    constructor(){
        this.code = 1011;
        this.message = "User has already signed in!";
        this.name = 'Already Signed In Error';
        this.status = 400;
    }
}
