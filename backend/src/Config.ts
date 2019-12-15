import * as dotenv from 'dotenv';

dotenv.config();
export const config = {
    DB_CONNECT: process.env.DB_CONNECT || "mongodb://localhost/catan",
    PORT: process.env.PORT || 3000,
    SECRET_KEY: process.env.SECRET_KEY || "secret"
};

