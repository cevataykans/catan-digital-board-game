import {UserController} from "./UserController";
import {Request, Response} from "express";
import { TokenService } from "./TokenService";

export class UserRoutes{
    private userController: UserController;
    private tokenService: TokenService;

    constructor(){
        this.userController = new UserController();
        this.tokenService = new TokenService();
    }

    public routes(app){
        app.route('/api/user/register')
            .post((req: Request, res: Response) => {
                this.userController.register(req, res)
            });

        app.route('/api/user/login')
            .post((req: Request, res: Response) => {
                this.userController.login(req, res)
            });

        app.route('/api/user/changePassword')
            .post(this.tokenService.checkToken, (req: Request, res: Response) => {
                this.userController.changePassword(req, res);
            });

        app.route('/api/user/logout')
            .post(this.tokenService.checkToken, (req: Request, res: Response) => {
                this.userController.logout(req, res);
            });

        app.route('/api/get') // For test purposes, should be deleted ***************************************
        .post((req, res) => {
            res.send("Hello");
            console.log("Hello");
        })    
    }

}
