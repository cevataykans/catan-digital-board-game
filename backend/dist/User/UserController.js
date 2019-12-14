"use strict";
var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : new P(function (resolve) { resolve(result.value); }).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
Object.defineProperty(exports, "__esModule", { value: true });
const UserBusiness_1 = require("./UserBusiness");
const UserValidation_1 = require("./UserValidation");
const ErrorResponse_1 = require("../ErrorResponse");
const UserMapping_1 = require("./UserMapping");
class UserController {
    constructor() {
        this.userBusiness = new UserBusiness_1.UserBusiness();
        this.validation = new UserValidation_1.UserValidation();
    }
    login(request, response) {
        return __awaiter(this, void 0, void 0, function* () {
            try {
                this.validation.loginRegisterValidation(request);
                let result = yield this.userBusiness.login(UserMapping_1.default.map(request.body));
                response.status(result.status).send(result);
            }
            catch (error) {
                const errorResponse = new ErrorResponse_1.ErrorResponse(error);
                response.status(errorResponse.status).send(new ErrorResponse_1.ErrorResponse(error));
            }
        });
    }
    register(request, response) {
        return __awaiter(this, void 0, void 0, function* () {
            try {
                console.log(request.body);
                this.validation.loginRegisterValidation(request);
                let result = yield this.userBusiness.register(UserMapping_1.default.map(request.body));
                response.status(result.status).send(result);
            }
            catch (error) {
                const errorResponse = new ErrorResponse_1.ErrorResponse(error);
                response.setHeader('Content-Type', 'application/json').status(errorResponse.status).send(new ErrorResponse_1.ErrorResponse(error));
            }
        });
    }
    changePassword(request, response) {
        return __awaiter(this, void 0, void 0, function* () {
            try {
                this.validation.changePasswordValidation(request);
                let result = yield this.userBusiness.changePassword(UserMapping_1.default.map(request.body));
            }
            catch (error) {
                const errorResponse = new ErrorResponse_1.ErrorResponse(error);
                response.status(errorResponse.status).send(new ErrorResponse_1.ErrorResponse(error));
            }
        });
    }
}
exports.UserController = UserController;
//# sourceMappingURL=UserController.js.map