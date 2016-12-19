import {
    Component, Input, Output, ElementRef, OnInit, OnChanges, SimpleChanges, EventEmitter, ViewContainerRef
} from '@angular/core';
import { Subject } from 'rxjs/Subject';
import * as grammar from '../../../service/command/grammar';
import * as imm from 'immutable';
import { Overlay } from 'angular2-modal';
import { Modal } from 'angular2-modal/plugins/bootstrap';
import {
    TicketOverviewTag,
    TicketOverviewUser
} from '../../ticket-overview/ticket-overview';
import { TicketFilter } from './ticket-filter';
import * as moment from 'moment';


export type CommentTextviewSaveEvent = {
    commands: imm.List<grammar.Cmd>,
    text: string,
};

@Component({
    selector: 'tt-ticket-filter',
    templateUrl: './ticket-filter.component.html',
    styleUrls: ['./ticket-filter.component.scss']
})
export class TicketFilterComponent implements OnInit, OnChanges {
    @Input() allUsers: imm.Map<string, TicketOverviewUser>;
    @Input() allTicketTags: imm.Map<string, TicketOverviewTag>;
    @Input() defaultFilterOpen: boolean = false;
    @Input() disabledFilterHelper: string = '';
    @Input() addToQuery: string = '';
    @Output() ticketFilter = new EventEmitter<TicketFilter>();
    public query: string = '';
    public elementRef: ElementRef;
    public filterHelper = false;
    public error = false;
    public errorMsg = '';
    private searchTerms = new Subject<string>();
    public ticketNumbers: string;
    public assignees: imm.List<TicketOverviewUser>;
    public tags: imm.List<TicketOverviewTag>;
    public datePickOneActive: boolean = false;
    public datePickTwoActive: boolean = false;
    public datePickOne: string;
    public datePickTwo: string;
    public datePickOneDate: Date;
    public datePickTwoDate: Date;
    public progressPickOne: number;
    public progressPickTwo: number;
    public spPickOne: number;
    public spPickTwo: number;
    public dateMode: string = '< Smaller Than';

    constructor(myElement: ElementRef, overlay: Overlay, vcRef: ViewContainerRef, public modal: Modal) {
        this.elementRef = myElement;
        overlay.defaultViewContainer = vcRef;
    }

    ngOnChanges(changes: SimpleChanges) {
        for (let propName in changes) {
            if (propName === 'addToQuery') {
                let chng = changes[propName];
                let cur = chng.currentValue;
                if (cur !== '') {
                    this.query = this.query + ' ' + cur;
                    this.filter(this.query);
                }
            }
        }
    }

    ngOnInit(): void {
        this.tags = this.allTicketTags.toList();
        this.assignees = this.allUsers.toList();
        this.filterHelper = this.defaultFilterOpen;
        this.searchTerms
            .distinctUntilChanged()
            .subscribe(term => this.filter(term), error => { });
    }

    onManualClick() {
        this.modal.alert()
            .size('lg')
            .showClose(true)
            .title('Filter Manual')
            .body(`
                    <h3>Filter ticket title</h3>
                        <p>Some ticket title</p>
                    <h3>Filter ticket number</h3>
                        <p>!#:1 </p>
                    <h3>Filter ticket tag</h3>
                        <p>!tag:name_1,name_2,...,name_n</p>
                    <h3>Filter ticket assignees</h3>
                        <p>!user:name_1,name_2,...,name_n </p>
                    <h3>Filter story points</h3>
                        <p>!sp:1-10 </p>
                        <p>!sp:&gt;10 </p>
                        <p>!sp:&lt;10</p>
                        <p>!sp:10</p>
                    <h3>Filter due dates</h3>
                        <p>!dueDate:1970-01-01/2038-01-19 </p>
                        <p>!dueDate:&gt;2038-01-19 </p>
                        <p>!dueDate:&lt;2038-01-19</p>
                        <p>!dueDate:2038-01-19</p>
                    <h3>Filter progress</h3>
                        <p>!progress:1%-10% </p>
                        <p>!progress:&gt;10% </p>
                        <p>!progress:&lt;10%</p>
                        <p>!progress:10%</p>
                        <p>Note: also works with 0.1 instead of 10%</p>
                    <h3>Filter StoryPoints</h3>
                        <p>!sp:1-10 </p>
                        <p>!sp:&gt;10 </p>
                        <p>!sp:&lt;10</p>
                        <p>!sp:10</p>
                    <h3>Filter Status</h3>
                        <p>!open:true </p>
        `)
            .open();
    }
    inputChanged(query: string): void {
        this.searchTerms.next(query);
    }


    filter(query: string) {
        // let filter: HTMLInputElement = this.elementRef.nativeElement.querySelector('#filterInput');
        this.error = false;
        this.errorMsg = '';
        let queryArray = query.split(' ');
        let title = '';
        let ticketNumbers: number[];
        let tags: string[] = [];
        let users: string[] = [];
        let progressOne: number;
        let progressTwo: number;
        let progressGreater: boolean;
        let dueDateOne: number;
        let dueDateTwo: number;
        let dueDateGreater: boolean;
        let storyPointsOne: number;
        let storyPointsTwo: number;
        let storyPointsGreater: boolean;
        let open: boolean;

        queryArray.forEach(e => {
            if (e.charAt(0) !== '!') {
                if (title.length === 0) {
                    title = e;
                    return;
                } else {
                    title = title + ' ' + e;
                    return;
                }
            } else {
                let command = e.split(':');
                if (command[1] && command[1].length > 0) {
                    if (command[0].indexOf('!#') === 0) {
                        if (ticketNumbers !== undefined) {
                            this.generateErrorAndMessage('only one !# allowed', command[0], undefined);
                            return;
                        }
                        ticketNumbers = [];
                        command[1].split(',').forEach( n => {
                            let tempNr = parseInt(n, 10);
                        if (tempNr === tempNr && tempNr) {
                            ticketNumbers.push(tempNr);
                            return;
                        } else {
                            this.generateErrorAndMessage('invalid number', command[0], command[1]);
                            return;
                        }
                        });

                    } else if (command[0].indexOf('!tag') === 0) {
                        command[1].split(',').forEach(t => {
                            tags.push(t);
                        });
                    } else if (command[0].indexOf('!user') === 0) {
                        command[1].split(',').forEach(t => {
                            users.push(t);
                        });
                    } else if (command[0].indexOf('!progress') === 0) {
                        if (progressOne !== undefined) {
                            this.generateErrorAndMessage('only one !progress allowed', command[0], undefined);
                            return;
                        }
                        let prog = command[1].split('-');
                        if (prog[1] && prog[1].length !== 0) { // ProgressFrom-ProgressTo
                            progressOne = parseFloat(prog[0]) / (prog[0].indexOf('%') > 0 ? 100 : 1);
                            progressTwo = parseFloat(prog[1]) / (prog[1].indexOf('%') > 0 ? 100 : 1);
                            return;
                        } else { // <Progress or >Progress or Progress
                            if (prog[0].charAt(0) === '>' && prog[0].length > 1) {
                                progressGreater = true;
                                prog[0] = prog[0].substr(1);
                            } else if (prog[0].charAt(0) === '<' && prog[0].length > 1) {
                                progressGreater = false;
                                prog[0] = prog[0].substr(1);
                            }
                            progressOne = parseFloat(prog[0]) / (prog[0].indexOf('%') > 0 ? 100 : 1);
                            return;
                        }
                    } else if (command[0].indexOf('!dueDate') === 0) {
                        if (dueDateOne !== undefined) {
                            this.generateErrorAndMessage('only one !dueDate allowed', command[0], undefined);
                            return;
                        }
                        let date: string[];
                        if ('<>'.indexOf(command[1].charAt(0)) >= 0) {
                            date = [command[1].substr(0, 11), command[1].substr(12, 10)];
                        } else {
                            date = [command[1].substr(0, 10), command[1].substr(11, 10)];
                        }
                        const regexp = new RegExp('\\d{4}-\\d{2}-\\d{2}');
                        if (date[1] && date[1].length !== 0) { // DateFrom-DateTo  
                            const m1 = moment(date[0], 'YYYY-MM-DD');
                            const m2 = moment(date[1], 'YYYY-MM-DD');
                            if (regexp.test(date[0]) && m1.isValid() && regexp.test(date[1]) && m2.isValid()) {
                                dueDateOne = m1.valueOf() + 3600000; // Todo:clean fix
                                dueDateTwo = m2.valueOf() + 3600000;
                                return;
                            } else {
                                this.generateErrorAndMessage('invalid ', command[0], command[1]);
                                return;
                            }
                        } else { // <Date or >Date or Date
                            if (date[0].charAt(0) === '>' && date[0].length > 1) {
                                dueDateGreater = true;
                                date[0] = date[0].substr(1);
                            } else if (date[0].charAt(0) === '<' && date[0].length > 1) {
                                dueDateGreater = false;
                                date[0] = date[0].substr(1);
                            }
                            const m1 = moment(date[0], 'YYYY-MM-DD');
                            if (regexp.test(date[0]) && m1.isValid()) {
                                dueDateOne = m1.valueOf() + 3600000; // Todo: clean fix 
                                return;
                            } else {
                                this.generateErrorAndMessage('invalid ', command[0], command[1]);
                                return;
                            }
                        }
                    } else if (command[0].indexOf('!sp') === 0) {
                        if (storyPointsOne !== undefined) {
                            this.generateErrorAndMessage('only one !sp allowed', command[0], undefined);
                            return;
                        }
                        let sp = command[1].split('-');
                        if (sp[1] && sp[1].length !== 0) { // SPFromSPTo
                            let spOne = parseInt(sp[0], 10);
                            let spTwo = parseInt(sp[1], 10);
                            if (spOne === spOne && spTwo === spTwo) {
                                storyPointsOne = spOne;
                                storyPointsTwo = spTwo;
                                return;
                            } else {
                                this.generateErrorAndMessage('invalid ', command[0], command[1]);
                                return;
                            }
                        } else { // <SP or >SP or SP
                            if (sp[0].charAt(0) === '>' && sp[0].length > 1) {
                                storyPointsGreater = true;
                                sp[0] = sp[0].substr(1);
                            } else if (sp[0].charAt(0) === '<' && sp[0].length > 1) {
                                storyPointsGreater = false;
                                sp[0] = sp[0].substr(1);
                            }
                            if (parseInt(sp[0], 10) === parseInt(sp[0], 10)) {
                                storyPointsOne = parseInt(sp[0], 10);
                                return;
                            } else {
                                this.generateErrorAndMessage('invalid ', command[0], command[1]);
                                return;
                            }
                        }
                    } else if (command[0].indexOf('!open') === 0) {
                        if (open !== undefined) {
                            this.generateErrorAndMessage('only one !open allowed', command[0], undefined);
                            return;
                        }
                        if (command[1] === 'true') {
                            open = true;
                        } else if (command[1] === 'false') {
                            open = false;
                        }
                    } else {
                        this.generateErrorAndMessage('invalid command', command[0], command[1]);
                        return;
                    }
                } else {
                    this.generateErrorAndMessage('invalid command', command[0], '');
                    return;
                }

            }
        });
        let finalFilter = new TicketFilter(title, ticketNumbers, tags, users, progressOne, progressTwo,
            progressGreater, dueDateOne, dueDateTwo, dueDateGreater, storyPointsOne, storyPointsTwo,
            storyPointsGreater, open);
        this.ticketFilter.emit(finalFilter);
    }

    generateErrorAndMessage(msg: string, cmd: string, value: string) {
        this.error = true;
        if (this.errorMsg.length === 0) {
            msg = msg.charAt(0).toUpperCase() + msg.slice(1);
            this.errorMsg = msg + (value === undefined ? '' : ' ' + cmd + ':' + value);
        } else {
            msg = msg.charAt(0).toLowerCase() + msg.slice(1);
            this.errorMsg += ' and ' + msg + (value === undefined ? '' : ' ' + cmd + ':' + value);
        }
        return;
    }
    pickNumbers(numbers: string) {
        this.query = this.query.split(' ').filter(e => e.indexOf('!#') < 0).join(' ') + ' !#:' + numbers;
        this.filter(this.query);
    }
    pickTag(tagname: string) {
        this.query = this.query + ' !tag:' + tagname;
        this.filter(this.query);
    }
    pickUser(username: string) {
        this.query = this.query + ' !user:' + username;
        this.filter(this.query);
    }
    pickSP(op: string) {
        console.log();
        // Split query at ' ', remove all elements in this array containing !sp and rejoin the array with ' ' to a string. 
        // Then add the new !sp command  
        this.query = this.query.split(' ').filter(e => e.indexOf('!sp') < 0).join(' ') + ' !sp:' + ((op === '-' || op === '=') ? '' : op)
            + this.spPickOne + (op === '-' ? op + this.spPickTwo : '');
        this.filter(this.query);
    }
    pickDate(op: string) {
        // Split query at ' ', remove all elements in this array containing !dueDate and rejoin the array with ' ' to a string. 
        // Then add the new !dueDate command  
        this.query = this.query.split(' ').filter(e => e.indexOf('!dueDate') < 0).join(' ')
            + ' !dueDate:' + ((op === '-' || op === '=') ? '' : op) + this.datePickOne +
            (op === '-' ? op + this.datePickTwo : '');
        this.filter(this.query);
    }
    pickProgress(op: string) {
        // Split query at ' ', remove all elements in this array containing !progress and rejoin the array with ' ' to a string. 
        // Then add the new !progress command  
        this.query = this.query.split(' ').filter(e => e.indexOf('!progress') < 0).join(' ')
            + ' !progress:' + ((op === '-' || op === '=') ? '' : op) + this.progressPickOne + '%'
            + (op === '-' ? op + this.progressPickTwo + '%' : '');
        this.filter(this.query);
    }
    pickStatus(open: boolean) {
        // Split query at ' ', remove all elements in this array containing !open and rejoin the array with ' ' to a string. 
        // Then add the new !open command  
        this.query = this.query.split(' ').filter(e => e.indexOf('!open') < 0).join(' ') + (open === undefined ? '' : ' !open:' + open);
        this.filter(this.query);
    }

    datePickerOneSelection() { // TODO: fix Localtime/UTC
        let m = moment(this.datePickOneDate);
        this.datePickOne = m.local().format('YYYY-MM-DD');
    }
    datePickerTwoSelection() {
        let m = moment(this.datePickTwoDate);
        this.datePickTwo = m.local().format('YYYY-MM-DD');
    }
    checkProgressFormInvalid(progressMode: string): boolean {
        return (this.progressPickOne === undefined || this.progressPickOne === null)
            || progressMode === '- Between' && (this.progressPickTwo === undefined || this.progressPickTwo === null);
    }
    checkDateFormInvalid(): boolean {
        return (this.datePickOne === undefined || this.datePickOne === '') ||
            this.dateMode === '- Between' && (this.datePickTwo === undefined || this.datePickTwo === '');
    }
    checkSPFormInvalid(spMode: string): boolean {
        return (this.spPickOne === undefined || this.spPickOne === null) ||
            spMode === '- Between' && (this.spPickTwo === undefined || this.spPickTwo === null);
    }
}

