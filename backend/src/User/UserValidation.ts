import * as joi from '@hapi/joi';
import {InvalidUserInformation} from "./InvalidUserInformation";
import {Request} from "express";

export class UserValidation{

    public loginRegisterValidation(req: Request): void {
        if(!req.body.userId || !req.body.password)
            throw new InvalidUserInformation();
    }

    public changePasswordValidation(req: Request): void {
        if(!req.body.password)
            throw new InvalidUserInformation();
        
    }
}
