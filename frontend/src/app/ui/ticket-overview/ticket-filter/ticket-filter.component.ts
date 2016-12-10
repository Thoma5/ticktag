import {
    Component, Input, Output, ElementRef, OnInit, EventEmitter, ViewContainerRef
} from '@angular/core';
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
}

@Component({
    selector: 'tt-ticket-filter',
    templateUrl: './ticket-filter.component.html',
    styleUrls: ['./ticket-filter.component.scss']
})
export class TicketFilterComponent implements OnInit {
    @Input() allUsers: imm.Map<string, TicketOverviewUser>;
    @Input() allTicketTags: imm.Map<string, TicketOverviewTag>;
    @Output() ticketFilter = new EventEmitter<TicketFilter>();

    public selected: string[] = [];
    public elementRef: ElementRef;
    public query: string = '';
    public filters: string[] = ['!#:', '!tag:', '!user:', '!progress:', '!timespan:',
        '!before:', '!since:', '!status', 'open', 'closed', 'dd/MM/yyyy'];
    public filteredList: string[] = [];

    constructor(myElement: ElementRef, overlay: Overlay, vcRef: ViewContainerRef, public modal: Modal) {
        this.elementRef = myElement;
        overlay.defaultViewContainer = vcRef;
    }

    ngOnInit(): void {
        this.allTicketTags.forEach(e => {
            this.filters.push('!tag:' + e.normalizedName);
        });
        this.allUsers.forEach(e => {
            this.filters.push('!user:' + e.username);

        });
    }
    select(item: string) {
        this.selected.push(item);
        this.query = '';
        this.filteredList = [];
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

    filter() {
        // let filter: HTMLInputElement = this.elementRef.nativeElement.querySelector('#filterInput');
        let queryArray = this.query.split(' ');
        let title = '';
        let ticketNumber: number;
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
                        let tempNr = parseInt(command[1], 10);
                        if (tempNr !== tempNr) {
                            ticketNumber = tempNr;
                        } else {
                            console.log('Invalid input for filter number command');
                        }

                    } else if (command[0].indexOf('!tag') === 0) {
                        command[1].split(',').forEach(t => {
                            tags.push(t);
                        });
                    } else if (command[0].indexOf('!user') === 0) {
                        command[1].split(',').forEach(t => {
                            users.push(t);
                        });
                    } else if (command[0].indexOf('!progress') === 0) {
                        let prog = command[1].split('-');
                        if (prog[1] && prog[1].length !== 0) { // ProgressFrom-ProgressTo
                            progressOne = parseFloat(prog[0]) / (prog[0].indexOf('%') > 0 ? 100 : 1);
                            progressTwo = parseFloat(prog[1]) / (prog[1].indexOf('%') > 0 ? 100 : 1);
                        } else { // <Progress or >Progress or Progress
                            if (prog[0].charAt(0) === '>' && prog[0].length > 1) {
                                progressGreater = true;
                                prog[0] = prog[0].substr(1);
                            } else if (prog[0].charAt(0) === '<' && prog[0].length > 1) {
                                progressGreater = false;
                                prog[0] = prog[0].substr(1);
                            }
                            progressOne = parseFloat(prog[0]) / (prog[0].indexOf('%') > 0 ? 100 : 1);
                        }
                    } else if (command[0].indexOf('!dueDate') === 0) {
                        let date = command[1].split('/');
                        const regexp = new RegExp('\\d{4}-\\d{2}-\\d{2}');
                        if (date[1] && date[1].length !== 0) { // DateFrom-DateTo  
                            const m1 = moment(date[0], 'YYYY-MM-DD');
                            const m2 = moment(date[1], 'YYYY-MM-DD');
                            if (regexp.test(date[0]) && m1.isValid() && regexp.test(date[1]) && m2.isValid()) {
                                dueDateOne = m1.valueOf();
                                dueDateTwo = m2.valueOf();
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
                                dueDateOne = m1.valueOf();
                            }
                        }
                    } else if (command[0].indexOf('!sp') === 0) {
                        let sp = command[1].split('-');
                        if (sp[1] && sp[1].length !== 0) { // SPFromSPTo
                            storyPointsOne = parseInt(sp[0], 10);
                            storyPointsTwo = parseInt(sp[1], 10);
                        } else { // <SP or >SP or SP
                            if (sp[0].charAt(0) === '>' && sp[0].length > 1) {
                                storyPointsGreater = true;
                                sp[0] = sp[0].substr(1);
                            } else if (sp[0].charAt(0) === '<' && sp[0].length > 1) {
                                storyPointsGreater = false;
                                sp[0] = sp[0].substr(1);
                            }
                            storyPointsOne = parseInt(sp[0], 10);
                        }
                    } else if (command[0].indexOf('!open') === 0) {
                        if (command[1] === 'true') {
                            open = true;
                        } else if (command[1] === 'false') {
                            open = false;
                        }
                    } else {
                        // invalid command
                        console.log('Invalid filter command!');
                    }
                }


            }



        });
        let finalFilter = new TicketFilter(title, ticketNumber, tags, users, progressOne, progressTwo,
            progressGreater, dueDateOne, dueDateTwo, dueDateGreater, storyPointsOne, storyPointsTwo,
            storyPointsGreater, open);
        this.ticketFilter.emit(finalFilter);
    }

}

