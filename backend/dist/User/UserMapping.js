"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
class UserMapping {
    map(userModel) {
        if (!userModel)
            return null;
        return {
            userId: userModel.userId || null,
            password: userModel.password || null
        };
    }
}
exports.default = new UserMapping();
//# sourceMappingURL=UserMapping.js.map