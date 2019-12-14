"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const Config_1 = require("./Config");
const mongoose = require("mongoose");
const Socket_1 = require("./Socket");
function startServer() {
    const PORT = Config_1.config.PORT;
    Socket_1.default.listen(PORT); // Server is on and socket listens the events
    mongoSetup(); // DB Setup
}
function mongoSetup() {
    let mongoUrl = Config_1.config.DB_CONNECT;
    mongoose.Promise = global.Promise;
    mongoose.connect(mongoUrl, { useNewUrlParser: true, useUnifiedTopology: true }, () => {
        console.log('Mongo is ready (' + mongoUrl + ')');
    });
}
startServer();
//# sourceMappingURL=Server.js.map