"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const joi = require("@hapi/joi");
const invalidInformation_1 = require("../../errors/validation/invalidInformation");
class RegisterValidation {
    constructor() {
        this.schema = {
            name: joi.string().required(),
            surname: joi.string().required(),
            gender: joi.string().required(),
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
exports.default = new RegisterValidation();
//# sourceMappingURL=registerValidation.js.map