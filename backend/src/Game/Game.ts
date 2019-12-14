export class Game{

    private players: string[]
    private turn: number;
    private gameId: number;

    constructor(socketIds: string[], id: number){
        this.turn = 0;
        this.players = [];
        socketIds.forEach((item) => {
            this.players.push(item);
        });
        this.gameId = id;
    }

    public endTurn(): void {
        this.turn = (this.turn + 1) % 4;
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

}