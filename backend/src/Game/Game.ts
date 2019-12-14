export class Game{

    private players: string[]
    private turn: number;
    private gameId: number;
    private phase: number;
    private order: boolean;

    constructor(socketIds: string[], id: number){
        this.turn = 0;
        this.players = [];
        socketIds.forEach((item) => {
            this.players.push(item);
        });
        this.gameId = id;
        this.phase = 0;
        this.order = true;
    }

    public endTurn(): void {
        if(this.phase == 0 && this.order){
            if(this.turn == 3){
                this.order = false;
                console.log("Again turn " + this.turn + "but order is " + this.order);
            }
            else{
                this.turn = (this.turn + 1) % 4;
                console.log("turn: " + this.turn + "order: " + this.order);
            }
        }
        else if(this.phase == 0){
            if(this.turn == 0){
                this.phase = 1;
                this.order = true;
                console.log("turn: " + this.turn + "order: " + this.order)
            }
            else{
                this.turn = (this.turn - 1) % 4;
                console.log("turn: " + this.turn + "order: " + this.order)
            }
        }
        else{
            this.turn = (this.turn + 1) % 4;
            console.log("turn: " + this.turn + "order: " + this.order)
        }
    }

    public isTurnOf(userId: string): boolean {
        if(this.players[this.turn] == userId)
            return true;
        return false;
    }

    public getId(): number {
        return this.gameId;
    }

    public getPlayersSockets(socketId: string): string[]{
        let result: string[] = [];
        let index = 0;
        this.players.forEach((item) => {
            if(item != socketId){
                result[index] = item;
                index++;
            }
        })
        if(index == 4)
            return null;
        return result;
    }

    public getAllPlayers(): string[]{
        return this.players;
    }

    public getCurrentPlayer(): string{
        return this.players[this.turn];
    }

}