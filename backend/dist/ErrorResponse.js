"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
class ErrorResponse {
    constructor(error) {
        this.result = {
            code: error.code || 9999,
            message: error.message
        };
        this.data = null;
        this.status = error.status || 400;
    }
}
exports.ErrorResponse = ErrorResponse;
//# sourceMappingURL=ErrorResponse.js.map