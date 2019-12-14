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
const UserModel_1 = require("../Models/User/UserModel");
const WrongEmailOrPassword_1 = require("./WrongEmailOrPassword");
const UserMapping_1 = require("./UserMapping");
const ExistingEmail_1 = require("../User/ExistingEmail");
const BcryptService_1 = require("./BcryptService");
const UserPasswordChangeError_1 = require("./UserPasswordChangeError");
class UserDBService {
    constructor() {
        this.bcryptService = new BcryptService_1.BcryptService();
    }
    login(userId, password) {
        return __awaiter(this, void 0, void 0, function* () {
            try {
                let result = yield UserModel_1.UserModel.findOne({ userId: userId });
                if (!result)
                    throw new WrongEmailOrPassword_1.WrongEmailOrPassword();
                yield this.bcryptService.comparePasswords(password, result.password);
                let user = UserMapping_1.default.map(result);
                return user;
            }
            catch (error) {
                throw error;
            }
        });
    }
    register(body) {
        return __awaiter(this, void 0, void 0, function* () {
            try {
                const newUser = new UserModel_1.UserModel(body);
                newUser.password = yield this.bcryptService.passwordHash(newUser.password);
                let result = yield newUser.save();
                return UserMapping_1.default.map(result);
            }
            catch (error) {
                throw new ExistingEmail_1.ExistingEmail();
            }
        });
    }
    changePassword(body) {
        return __awaiter(this, void 0, void 0, function* () {
            try {
                const data = {
                    password: body.password
                };
                let result = yield UserModel_1.UserModel.findOneAndUpdate({ userId: body.userId }, data, { new: true });
                if (!result)
                    throw new UserPasswordChangeError_1.UserPasswordChangeError();
            }
            catch (err) {
                throw new UserPasswordChangeError_1.UserPasswordChangeError();
            }
        });
    }
}
exports.UserDBService = UserDBService;
//# sourceMappingURL=UserDBService.js.map