export class TicketFilter {
    readonly title: string = '';
    readonly ticketNumbers: number[];
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


    constructor(title: string, ticketNumbers: number[], tags: string[], users: string[],
        progressOne: number, progressTwo: number, progressGreater: boolean,
        dueDateOne: number, dueDateTwo: number, dueDateGreater: boolean, storyPointsOne: number,
        storyPointsTwo: number, storyPointsGreater: boolean, open: boolean) {
        this.title = title === '' ? undefined : title;
        this.ticketNumbers = ticketNumbers ? ticketNumbers.filter(e => isFinite(e)) : undefined;
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

    toTicketFilterString(): string {
        let list: string[] = [];
        if (this.title) { list.push(this.title); }
        if (this.ticketNumbers) { list.push('!#:' + this.ticketNumbers); }
        if (this.tags) { list.push('!tag:' + this.tags); }
        if (this.users) { list.push('!user:' + this.users); }
        if (this.progressOne && this.progressTwo) {
            list.push('!progress:' + this.progressOne + '-' + this.progressTwo);
        } else if (this.progressOne && this.progressGreater !== undefined) {
            list.push('!progress:' + (this.progressGreater ? '>' : '<') + this.progressOne);
        } else if (this.progressOne && this.progressGreater === undefined) {
            list.push('!progress:' + this.progressOne);
        }
        if (this.dueDateOne && this.dueDateTwo) {
            list.push('!dueDate:' + this.dueDateOne + '-' + this.dueDateTwo);
        } else if (this.dueDateOne && this.dueDateGreater !== undefined) {
            list.push('!dueDate:' + (this.dueDateGreater ? '>' : '<') + this.dueDateOne);
        } else if (this.dueDateOne && this.dueDateGreater === undefined) {
            list.push('!dueDate:' + this.dueDateOne);
        }
        if (this.storyPointsOne && this.storyPointsTwo) {
            list.push('!sp:' + this.storyPointsOne + '-' + this.storyPointsTwo);
        } else if (this.storyPointsOne && this.storyPointsGreater !== undefined) {
            list.push('!sp:' + (this.storyPointsGreater ? '>' : '<') + this.storyPointsOne);
        } else if (this.storyPointsOne && this.storyPointsGreater === undefined) {
            list.push('!sp:' + this.storyPointsOne);
        }
        if (this.open !== undefined) { list.push('!open:' + this.open); }
        return list.join(' ');
    }
    toTicketFilterURLString(): string {
        let list: string[] = [];
        if (this.title) { list.push('title=' + this.title); }
        if (this.ticketNumbers) { list.push('ticketNumber=' + this.ticketNumbers); }
        if (this.tags) { list.push('tag=' + this.tags); }
        if (this.users) { list.push('user=' + this.users); }
        if (this.progressOne && this.progressTwo) {
            list.push('progressOne=' + this.progressOne);
            list.push('progressTwo=' + this.progressTwo);
        } else if (this.progressOne && this.progressGreater !== undefined) {
            list.push('progressOne=' + this.progressOne);
            list.push('progressGreater=' + this.progressGreater);
        }
            if (this.dueDateOne && this.dueDateTwo) {
            list.push('dueDateOne=' + this.dueDateOne);
            list.push('dueDateTwo=' + this.dueDateTwo);
        } else if (this.dueDateOne && this.dueDateGreater !== undefined) {
            list.push('dueDateOne=' + this.dueDateOne);
            list.push('dueDateGreater=' + this.dueDateGreater);
        }
            if (this.storyPointsOne && this.storyPointsTwo) {
            list.push('spOne=' + this.storyPointsOne);
            list.push('spTwo=' + this.storyPointsTwo);
        } else if (this.storyPointsOne && this.storyPointsGreater !== undefined) {
            list.push('spOne=' + this.storyPointsOne);
            list.push('spGreater=' + this.storyPointsGreater);
        }
        if (this.open !== undefined) { list.push('open=' + this.open); }
        return list.join('&');
    }
}
