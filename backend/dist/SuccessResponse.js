"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
class SuccessResponse {
    constructor(data) {
        this.result = {
            code: 0,
            message: "Success"
        };
        this.data = data;
        this.status = 200;
    }
}
exports.SuccessResponse = SuccessResponse;
//# sourceMappingURL=SuccessResponse.js.map