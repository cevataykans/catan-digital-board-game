"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const joi = require("@hapi/joi");
const invalidInformation_1 = require("../../errors/validation/invalidInformation");
class LoginValidation {
    constructor() {
        this.schema = {
            email: joi.string().required().email(),
            password: joi.string().required()
        };
    }
    check(req) {
        try {
            const { error } = joi.validate(req.body, this.schema);
            if (error)
                throw new invalidInformation_1.InvalidInformation();
        }
        catch (error) {
            throw error;
        }
    }
}
exports.default = new LoginValidation();
//# sourceMappingURL=loginValidation.js.map