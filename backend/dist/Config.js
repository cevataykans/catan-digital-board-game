"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const dotenv = require("dotenv");
dotenv.config();
exports.config = {
    DB_CONNECT: process.env.DB_CONNECT || "mongodb://localhost/catan",
    PORT: process.env.PORT || 3000,
    SECRET_KEY: process.env.SECRET_KEY || "secret"
};
//# sourceMappingURL=Config.js.map