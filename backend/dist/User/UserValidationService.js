"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const joi = require("@hapi/joi");
const InvalidInformation_1 = require("./InvalidInformation");
const InternalServerError_1 = require("./InternalServerError");
class UserValidationService {
    loginValidation(request) {
        const schema = {
            userId: joi.string().required(),
            password: joi.string().required()
        };
        try {
            const { error } = joi.validate(request.body, schema);
            if (error)
                throw new InvalidInformation_1.InvalidInformation();
        }
        catch (error) {
            if (error instanceof InvalidInformation_1.InvalidInformation) {
                throw error;
            }
            else {
                throw new InternalServerError_1.InternalServerError();
            }
        }
    }
    registerValidation(request) {
        const schema = {
            userId: joi.string().required(),
            email: joi.email().required(),
            password: joi.string().required()
        };
        try {
            const { error } = joi.validate(request.body, schema);
            if (error)
                throw new InvalidInformation_1.InvalidInformation();
        }
        catch (error) {
            if (error instanceof InvalidInformation_1.InvalidInformation) {
                throw error;
            }
            else {
                throw new InternalServerError_1.InternalServerError();
            }
        }
    }
}
exports.UserValidationService = UserValidationService;
//# sourceMappingURL=UserValidationService.js.map