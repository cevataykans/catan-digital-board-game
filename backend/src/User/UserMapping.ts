import {User} from "../Models/User/User";

class UserMapping{
    public map(userModel): User {
        if(!userModel) return null;
        return {
            userId: userModel.userId || null,
            password: userModel.password || null
        };
    }
}

export default new UserMapping();