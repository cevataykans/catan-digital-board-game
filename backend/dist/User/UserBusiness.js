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
const UserDBService_1 = require("./UserDBService");
const SuccessResponse_1 = require("../SuccessResponse");
const ErrorResponse_1 = require("../ErrorResponse");
class UserBusiness {
    constructor() {
        this.userDBService = new UserDBService_1.UserDBService();
    }
    login(user) {
        return __awaiter(this, void 0, void 0, function* () {
            try {
                const userId = user.userId;
                const password = user.password;
                let result = yield this.userDBService.login(userId, password);
                return new SuccessResponse_1.SuccessResponse(result);
            }
            catch (error) {
                return new ErrorResponse_1.ErrorResponse(error);
            }
        });
    }
    register(user) {
        return __awaiter(this, void 0, void 0, function* () {
            try {
                let result = yield this.userDBService.register(user);
                return new SuccessResponse_1.SuccessResponse(result);
            }
            catch (error) {
                return new ErrorResponse_1.ErrorResponse(error);
            }
        });
    }
    changePassword(user) {
        return __awaiter(this, void 0, void 0, function* () {
            try {
                let result = yield this.userDBService.changePassword(user);
                return new SuccessResponse_1.SuccessResponse(result);
            }
            catch (error) {
                return new ErrorResponse_1.ErrorResponse(error);
            }
        });
    }
}
exports.UserBusiness = UserBusiness;
//# sourceMappingURL=UserBusiness.js.map