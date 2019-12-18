export class GameQueue{

    public NUMBER_OF_PLAYERS: number = 4;

    private queue: any[];

    constructor(){
        this.queue = [];
    }

    public addPlayer(socketId: string, name: string): any[]{
        let alreadyInQueue: boolean = false;
        this.queue.forEach((item) => {
            if(item.userId == name)
                alreadyInQueue = true;
        })

        if(alreadyInQueue)
            return;

        if(this.queue.length < this.NUMBER_OF_PLAYERS - 1){
            const player = {
                "userId": name,
                "socketId": socketId
            };
            this.queue.push(player);
            return null;
        }
        let result: any = [];
        for(let i = 0 ; i < this.NUMBER_OF_PLAYERS - 1; i++){
            const player = this.queue.shift();
            result.push(player);
        }
        const newPlayer = {
            "userId": name,
            "socketId": socketId
        };
        result.push(newPlayer);
        console.log("Empty queue: " + this.queue);
        return result;
    }

    public deletePlayerFromQueue(socketId: string): boolean {
        console.log(this.queue)
        this.queue.forEach((item, index) => {
            if(item.socketId == socketId){
                this.queue.splice(index, 1);
                return true;
            }
        });
        console.log("deleted");
        console.log(this.queue);
        return false;
    }

    public getWaitingPlayers(): string[] {
        let result: string[] = [];
        this.queue.forEach((item) => {
            result.push(item.socketId);
        });
        return result;
    }

}