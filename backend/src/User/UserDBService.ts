import * as id from 'uuid/v1';
import {User} from "../Models/User/User";
import {UserModel} from "../Models/User/UserModel";
import {WrongEmailOrPassword} from "./WrongEmailOrPassword";
import userMapping from "./UserMapping";
import {ExistingEmail} from '../User/ExistingEmail';
import { BcryptService } from './BcryptService';
import { UserPasswordChangeError } from './UserPasswordChangeError';
import {AlreadySignedInError} from './AlreadySignedInError';
import {LogoutError} from './LogoutError';
import { TokenService } from './TokenService';

export class UserDBService {
    bcryptService: BcryptService;
    tokenService: TokenService

    constructor() {
        this.bcryptService = new BcryptService();
        this.tokenService = new TokenService();
    }

    public async login(userId: string, password: string): Promise<any> {
        try {
            let result = await UserModel.findOne({userId: userId});
            if(!result) throw new WrongEmailOrPassword();
            await this.bcryptService.comparePasswords(password, result.password);
            if(result.online)
                throw new AlreadySignedInError();
            let user = userMapping.map(result);
            await UserModel.findOneAndUpdate({userId: userId}, {online: true}, {new: true});
            let token = await this.tokenService.signIn(userId);
            return {token: token};
        } catch(error) {
            throw error;
        }
    }

    public async register(body: User): Promise<void> {
        try {
            const newUser = new UserModel(body);
            newUser.password = await this.bcryptService.passwordHash(newUser.password);
            await newUser.save();
        } catch (error) {
            throw new ExistingEmail();
        }

    }

    public async changePassword(body: User): Promise<void> {
        try{
            const newPassword = await this.bcryptService.passwordHash(body.password);
            const data = {
                password: newPassword
            };
            console.log("New psw: " + newPassword);
            let user = await UserModel.findOne({userId: body.userId});
            if(!user.online)
                return;
            let result = await UserModel.findOneAndUpdate({userId: body.userId}, data, {new: true});
            if(!result) throw new UserPasswordChangeError();
        } catch(err){
            throw new UserPasswordChangeError();
        }
    }

    public async logout(body: User): Promise<void> {
        try{
            let result = await UserModel.findOneAndUpdate({userId: body.userId}, {online: false}, {new: true});
            if(!result) throw new LogoutError();
        } catch(err){
            throw new LogoutError();
        }
    }

}
