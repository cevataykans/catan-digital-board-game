"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const UserController_1 = require("./UserController");
class UserRoutes {
    constructor() {
        this.userController = new UserController_1.UserController();
    }
    routes(app) {
        app.route('/api/user/register')
            .post((req, res) => {
            this.userController.register(req, res);
        });
        app.route('/api/user/login')
            .post((req, res) => {
            this.userController.login(req, res);
        });
    }
}
exports.UserRoutes = UserRoutes;
//# sourceMappingURL=UserRoutes.js.map