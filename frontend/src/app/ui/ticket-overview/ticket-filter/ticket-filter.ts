export class TicketFilter {
    readonly title = '';
    readonly ticketNumber: number;
    readonly tags: string[];
    readonly users: string[];
    readonly progressOne: number;
    readonly progressTwo: number;
    readonly progressGreater: boolean;
    readonly dueDateOne: number;
    readonly dueDateTwo: number;
    readonly dueDateGreater: boolean;
    readonly storyPointsOne: number;
    readonly storyPointsTwo: number;
    readonly storyPointsGreater: boolean;
    readonly open: boolean;


    constructor(title: string, ticketNumber: number, tags: string[], users: string[],
        progressOne: number, progressTwo: number, progressGreater: boolean,
        dueDateOne: number, dueDateTwo: number, dueDateGreater: boolean, storyPointsOne: number,
        storyPointsTwo: number, storyPointsGreater: boolean, open: boolean) {
        this.title = title === '' ? undefined : title;
        this.ticketNumber = ticketNumber !== ticketNumber ? undefined : ticketNumber;
        this.ticketNumber = ticketNumber !== ticketNumber ? undefined : ticketNumber;
        this.ticketNumber = ticketNumber !== ticketNumber ? undefined : ticketNumber;
        this.tags = (tags ? tags : []).length > 0 ? tags : undefined;
        this.users = (users ? users : []).length > 0 ? users : undefined;
        this.progressOne = progressOne !== progressOne ? undefined : progressOne;
        this.progressTwo = progressTwo !== progressTwo ? undefined : progressTwo;
        this.progressGreater = progressGreater;
        this.dueDateOne = dueDateOne !== dueDateOne ? undefined : dueDateOne;
        this.dueDateTwo = dueDateTwo !== dueDateTwo ? undefined : dueDateTwo;
        this.dueDateGreater = dueDateGreater;
        this.storyPointsOne = storyPointsOne;
        this.storyPointsTwo = storyPointsTwo;
        this.storyPointsGreater = storyPointsGreater;
        this.open = open;
    }
}