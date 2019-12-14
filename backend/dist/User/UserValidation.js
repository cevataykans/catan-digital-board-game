"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const InvalidUserInformation_1 = require("./InvalidUserInformation");
class UserValidation {
    loginRegisterValidation(req) {
        if (!req.body.userId || !req.body.password)
            throw new InvalidUserInformation_1.InvalidUserInformation();
    }
    changePasswordValidation(req) {
        if (!req.body.password)
            throw new InvalidUserInformation_1.InvalidUserInformation();
    }
}
exports.UserValidation = UserValidation;
//# sourceMappingURL=UserValidation.js.map