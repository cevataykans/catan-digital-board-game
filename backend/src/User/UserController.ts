import {Request, Response} from "express";
import {UserBusiness} from "./UserBusiness";
import {UserValidation} from "./UserValidation";
import {ErrorResponse} from "../ErrorResponse";
import userMapping from "./UserMapping";


export class UserController{

    private userBusiness: UserBusiness;
    private validation: UserValidation;

    constructor() {
        this.userBusiness = new UserBusiness();
        this.validation = new UserValidation();
    }

    public async login(request: Request, response: Response): Promise<void> {
        try{
            this.validation.loginRegisterValidation(request);
            let result = await this.userBusiness.login(userMapping.map(request.body));
            response.status(result.status).send(result);
        } catch(error){
            const errorResponse = new ErrorResponse(error);
            response.status(errorResponse.status).send(new ErrorResponse(error));
        }

    }

    public async register(request: Request, response: Response): Promise<void> {
        try{
            console.log(request.body);
            this.validation.loginRegisterValidation(request);
            let result = await this.userBusiness.register(userMapping.map(request.body));
            response.status(result.status).send(result);
        } catch(error){
            const errorResponse = new ErrorResponse(error);
            response.setHeader('Content-Type', 'application/json').status(errorResponse.status).send(new ErrorResponse(error));
        }
    }

    public async changePassword(request: Request, response: Response): Promise<void> {
        try{
            this.validation.changePasswordValidation(request);
            let result = await this.userBusiness.changePassword(userMapping.map(request.body));
            response.status(result.status).send(result);
        } catch(error){
            const errorResponse = new ErrorResponse(error);
            response.status(errorResponse.status).send(new ErrorResponse(error));
        }
    }

    public async logout(request: Request, response: Response): Promise<void> {
        try{
            this.validation.logout(request);
            let result = await this.userBusiness.logout(userMapping.map(request.body));
            response.status(result.status).send(result);
        } catch(error){
            const errorResponse = new ErrorResponse(error);
            response.status(errorResponse.status).send(new ErrorResponse(error));
        }
    }
}

