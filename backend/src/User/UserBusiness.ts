import {UserDBService} from "./UserDBService";
import {SuccessResponse} from "../SuccessResponse";
import {ErrorResponse} from "../ErrorResponse";
import {ResponseModel} from "../Models/Response/ResponseModel";
import { User } from "../Models/User/User";

export class UserBusiness {

    private userDBService: UserDBService;

    constructor(){
        this.userDBService = new UserDBService();
    }

    public async login(user: User): Promise<ResponseModel> {
        try {
            const userId = user.userId;
            const password = user.password;
            let result = await this.userDBService.login(userId, password);
            return new SuccessResponse(result);
        } catch(error) {
            return new ErrorResponse(error);
        }
    }

    public async register(user: User): Promise<ResponseModel> {
        try {
            let result = await this.userDBService.register(user);
            return new SuccessResponse(result);
        } catch (error) {
            return new ErrorResponse(error);
        }
    }

    public async changePassword(user: User): Promise<ResponseModel> {
        try {
            let result = await this.userDBService.changePassword(user);
            return new SuccessResponse(result);
        } catch (error) {
            return new ErrorResponse(error);
        }
    }

    public async logout(user: User): Promise<ResponseModel> {
        try {
            let result = await this.userDBService.logout(user);
            return new SuccessResponse(result);
        } catch (error) {
            return new ErrorResponse(error);
        }
    }
}
