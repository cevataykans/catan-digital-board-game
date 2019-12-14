"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const express = require("express");
const bodyparser = require("body-parser");
const UserRoutes_1 = require("./User/UserRoutes");
class App {
    constructor() {
        this.app = express();
        this.server = require('http').createServer(this.app);
        this.config();
        this.userRoutes();
    }
    config() {
        this.app.use(bodyparser.json());
        this.app.use(bodyparser.urlencoded({ extended: false }));
    }
    userRoutes() {
        const userRoutes = new UserRoutes_1.UserRoutes();
        userRoutes.routes(this.app);
    }
}
exports.default = new App().server;
//# sourceMappingURL=App.js.map