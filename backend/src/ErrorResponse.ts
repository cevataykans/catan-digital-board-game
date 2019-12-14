import {ResponseModel} from "./Models/Response/ResponseModel";
import {Result} from "./Models/Response/ResultModel";
import {ErrorModel} from "./Models/Error/Error";

export class ErrorResponse implements ResponseModel{

    result: Result;
    data: null;
    status: number;

    constructor(error: ErrorModel) {
        this.result = {
            code: error.code || 9999,
            message: error.message
        };
        this.data = null;
        this.status = error.status || 400;
    }

}
