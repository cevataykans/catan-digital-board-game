import {config} from "./Config";
import * as mongoose from 'mongoose';
import socket from "./Socket";

function startServer(){
    const PORT: number = <number>config.PORT;
    socket.listen(PORT); // Server is on and socket listens the events
    mongoSetup(); // DB Setup
}

function mongoSetup(){
    let mongoUrl = <string>config.DB_CONNECT;
    mongoose.Promise = global.Promise;
    mongoose.connect(mongoUrl , {useNewUrlParser : true, useUnifiedTopology: true} , () => {
        console.log('Mongo is ready (' + mongoUrl + ')');
    })
}

startServer();