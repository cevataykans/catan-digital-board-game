"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const jwt = require("jsonwebtoken");
const NoAccess_1 = require("../User/NoAccess");
const ErrorResponse_1 = require("../ErrorResponse");
const Config_1 = require("../Config");
class TokenService {
    signIn(userId) {
        const secret_key = Config_1.config.SECRET_KEY;
        return jwt.sign({ userId: userId }, secret_key);
    }
    checkToken(req, res, next) {
        try {
            const header = req.header('Authorization');
            if (!header) {
                throw new NoAccess_1.NoAccess();
            }
            const headerArr = header.split(' ');
            if (headerArr.length < 2) {
                throw new NoAccess_1.NoAccess();
            }
            const token = headerArr[1];
            if (!token) {
                throw new NoAccess_1.NoAccess();
            }
            const secret_key = Config_1.config.SECRET_KEY;
            const verified = jwt.verify(token, secret_key);
            if ((verified.userId !== req.body.userId) && (verified.userId !== req.params.userId)) {
                throw new NoAccess_1.NoAccess();
            }
            next();
        }
        catch (err) {
            const errorResponse = new ErrorResponse_1.ErrorResponse(new NoAccess_1.NoAccess());
            res.status(errorResponse.status).send(errorResponse);
        }
    }
}
exports.TokenService = TokenService;
//# sourceMappingURL=TokenService.js.map