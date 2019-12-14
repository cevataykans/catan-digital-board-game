import * as express from 'express';
import * as bodyparser from 'body-parser';
import {UserRoutes} from "./User/UserRoutes";

class App{
    app: express.Application;
    server;

    constructor(){
        this.app = express();
        this.server = require('http').createServer(this.app);
        this.config();
        this.userRoutes();
    }

    private config(){
        this.app.use(bodyparser.json());
        this.app.use(bodyparser.urlencoded({extended: false}));
    }

    private userRoutes(){
        const userRoutes = new UserRoutes();
        userRoutes.routes(this.app);
    }

}

export default new App().server;
