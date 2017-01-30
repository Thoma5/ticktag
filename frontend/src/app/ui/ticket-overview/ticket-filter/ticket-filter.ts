import * as moment from 'moment';

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
    readonly parentNumber: number;


    constructor(title: string, ticketNumbers: number[], tags: string[], users: string[],
        progressOne: number, progressTwo: number, progressGreater: boolean,
        dueDateOne: number, dueDateTwo: number, dueDateGreater: boolean, storyPointsOne: number,
        storyPointsTwo: number, storyPointsGreater: boolean, open: boolean, parentNumber: number) {
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
        this.parentNumber = parentNumber;
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
            list.push(
                '!dueDate:' + moment(this.dueDateOne, 'x').format('YYYY-MM-DD') + '-' + moment(this.dueDateTwo, 'x').format('YYYY-MM-DD')
                );
        } else if (this.dueDateOne && this.dueDateGreater !== undefined) {
            list.push('!dueDate:' + (this.dueDateGreater ? '>' : '<') + moment(this.dueDateOne, 'x').format('YYYY-MM-DD'));
        } else if (this.dueDateOne && this.dueDateGreater === undefined) {
            list.push('!dueDate:' + moment(this.dueDateOne, 'x').format('YYYY-MM-DD'));
        }
        if (this.storyPointsOne && this.storyPointsTwo) {
            list.push('!sp:' + this.storyPointsOne + '-' + this.storyPointsTwo);
        } else if (this.storyPointsOne && this.storyPointsGreater !== undefined) {
            list.push('!sp:' + (this.storyPointsGreater ? '>' : '<') + this.storyPointsOne);
        } else if (this.storyPointsOne && this.storyPointsGreater === undefined) {
            list.push('!sp:' + this.storyPointsOne);
        }
        if (this.open !== undefined) { list.push('!open:' + this.open); }
        if (this.parentNumber !== undefined) { list.push('!parent:' + ((this.parentNumber < 0) ? 'none' : this.parentNumber)); }
        let joinedList = list.join(' ');
        if (joinedList.length > 0 && joinedList.charAt(0) === ' ') {
            joinedList = joinedList.substring(1);
        }
        return joinedList;
    }
    toTicketFilterURLString(): string {
        let list: string[] = [];
        if (this.title) { list.push('title=' + encodeURIComponent(this.title)); }
        if (this.ticketNumbers) { list.push('ticketNumber=' + this.ticketNumbers); }
        if (this.tags) { list.push('tag=' + this.tags.map(e => encodeURIComponent(e))); }
        if (this.users) { list.push('user=' + this.users.map(e => encodeURIComponent(e))); }
        if (this.progressOne && this.progressTwo) {
            list.push('progressOne=' + this.progressOne);
            list.push('progressTwo=' + this.progressTwo);
        } else if (this.progressOne && this.progressGreater !== undefined) {
            list.push('progressOne=' + this.progressOne);
            list.push('progressGreater=' + this.progressGreater);
        } else if (this.progressOne && this.progressGreater === undefined) {
            list.push('progressOne=' + this.progressOne);
        }
        if (this.dueDateOne && this.dueDateTwo) {
            list.push('dueDateOne=' + moment(this.dueDateOne, 'x').valueOf());
            list.push('dueDateTwo=' + moment(this.dueDateTwo, 'x').valueOf());
        } else if (this.dueDateOne && this.dueDateGreater !== undefined) {
            list.push('dueDateOne=' + moment(this.dueDateOne, 'x').valueOf());
            list.push('dueDateGreater=' + this.dueDateGreater);
        } else if (this.dueDateOne && this.dueDateGreater === undefined) {
            list.push('dueDateOne=' + moment(this.dueDateOne, 'x').valueOf());
        }
        if (this.storyPointsOne && this.storyPointsTwo) {
            list.push('spOne=' + this.storyPointsOne);
            list.push('spTwo=' + this.storyPointsTwo);
        } else if (this.storyPointsOne && this.storyPointsGreater !== undefined) {
            list.push('spOne=' + this.storyPointsOne);
            list.push('spGreater=' + this.storyPointsGreater);
        } else if (this.storyPointsOne && this.storyPointsGreater === undefined) {
            list.push('spOne=' + this.storyPointsOne);
        }

        if (this.open !== undefined) { list.push('open=' + this.open); }
        if (this.parentNumber !== undefined) { list.push('parent=' + this.parentNumber); }
        return list.join('&');
    }
}
