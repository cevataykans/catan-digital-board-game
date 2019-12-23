import { ResponseModel } from "./Models/Response/ResponseModel";
import { Result } from './Models/Response/ResultModel';

export class SuccessResponse implements ResponseModel {

    result: Result; 
    data: null;
    status: number;

    constructor(data) {
        this.result = {
            code : 0,
            message : "Success"
        };
        this.data = data;
        this.status = 200;
    }
     
}
