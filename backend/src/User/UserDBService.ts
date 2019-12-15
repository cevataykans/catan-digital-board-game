import * as id from 'uuid/v1';
import {User} from "../Models/User/User";
import {UserModel} from "../Models/User/UserModel";
import {WrongEmailOrPassword} from "./WrongEmailOrPassword";
import userMapping from "./UserMapping";
import {ExistingEmail} from '../User/ExistingEmail';
import { BcryptService } from './BcryptService';
import { UserPasswordChangeError } from './UserPasswordChangeError';

export class UserDBService {
    bcryptService: BcryptService;

    constructor() {
        this.bcryptService = new BcryptService();
    }

    public async login(userId: string, password: string): Promise<User> {
        try {
            let result = await UserModel.findOne({userId: userId});
            if(!result) throw new WrongEmailOrPassword();
            await this.bcryptService.comparePasswords(password, result.password);
            let user = userMapping.map(result);
            return user;
        } catch(error) {
            throw error;
        }
    }

    public async register(body: User): Promise<User> {
        try {
            const newUser = new UserModel(body);
            newUser.password = await this.bcryptService.passwordHash(newUser.password);
            let result =  await newUser.save();
            return userMapping.map(result);
        } catch (error) {
            throw new ExistingEmail();
        }

    }

    public async changePassword(body: User): Promise<void> {
        try{
            const data = {
                password: body.password
            };
            let result = await UserModel.findOneAndUpdate({userId: body.userId}, data, {new: true});
            if(!result) throw new UserPasswordChangeError();
        } catch(err){
            throw new UserPasswordChangeError();
        }
    }

}
