import * as jwt from 'jsonwebtoken';
import {NoAccess} from "../User/NoAccess";
import {ErrorResponse} from "../ErrorResponse";
import {NextFunction, Request, Response} from "express";
import {config} from "../Config";

export class TokenService{
    signIn(userId: string){
        const secret_key = config.SECRET_KEY;
        return jwt.sign({userId : userId} , secret_key);
    }

    checkToken(req: Request, res:Response, next:NextFunction){
        try {
            const token = req.body.token;
            if (!token)
                throw new NoAccess();
            const secret_key = config.SECRET_KEY;
            const verified = jwt.verify(token, secret_key);
            if (verified.userId !== req.body.userId)
                throw new NoAccess();
            next();
        } catch(err){
            const errorResponse = new ErrorResponse(new NoAccess());
            res.status(errorResponse.status).send(errorResponse);
        }
    }

    checkTokenForGame(data): void{
        const token = data.token;
        if (!token)
            throw new NoAccess();
        const secret_key = config.SECRET_KEY;
        const verified = jwt.verify(token, secret_key);
        if (verified.userId !== data.userId)
            throw new NoAccess();
    }
}
