import * as bcrypt from "bcrypt";
import {WrongEmailOrPassword} from "./WrongEmailOrPassword";
import {InternalServerError} from "./InternalServerError";

export class BcryptService {


    public async comparePasswords(sourcePsw:string , targetPsw: string){
        try {
            let match = await bcrypt.compare(sourcePsw, targetPsw);
            if(!match) throw new WrongEmailOrPassword();
        } catch(error){
            if(error instanceof WrongEmailOrPassword){
                throw error;
            }
            else{
                throw new InternalServerError();
            }
        }
    }

    public async passwordHash(password:string): Promise<string> {

        try {
            let salt = await bcrypt.genSalt();
            return await bcrypt.hash(password, salt);
        } catch (error) {
            throw new InternalServerError();
        }
    }

}
