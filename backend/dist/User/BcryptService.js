"use strict";
var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
Object.defineProperty(exports, "__esModule", { value: true });
const bcrypt = require("bcrypt");
const WrongEmailOrPassword_1 = require("./WrongEmailOrPassword");
const InternalServerError_1 = require("./InternalServerError");
class BcryptService {
    comparePasswords(sourcePsw, targetPsw) {
        return __awaiter(this, void 0, void 0, function* () {
            try {
                let match = yield bcrypt.compare(sourcePsw, targetPsw);
                if (!match)
                    throw new WrongEmailOrPassword_1.WrongEmailOrPassword();
            }
            catch (error) {
                if (error instanceof WrongEmailOrPassword_1.WrongEmailOrPassword) {
                    throw error;
                }
                else {
                    throw new InternalServerError_1.InternalServerError();
                }
            }
        });
    }
    passwordHash(password) {
        return __awaiter(this, void 0, void 0, function* () {
            try {
                let salt = yield bcrypt.genSalt();
                return yield bcrypt.hash(password, salt);
            }
            catch (error) {
                throw new InternalServerError_1.InternalServerError();
            }
        });
    }
}
exports.BcryptService = BcryptService;
//# sourceMappingURL=BcryptService.js.map