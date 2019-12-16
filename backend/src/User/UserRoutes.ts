import {UserController} from "./UserController";
import {Request, Response} from "express";

export class UserRoutes{
    private userController: UserController;

    constructor(){
        this.userController = new UserController();
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
        app.route('/api/get')
        .get((req, res) => {
            res.send("Hello");
            console.log("Hello");
        })    
    }

}
