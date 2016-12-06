import {
    Component, Input, ElementRef
} from '@angular/core';
import * as grammar from '../../../service/command/grammar';
import * as imm from 'immutable';
import {
    TicketOverviewAssTag, TicketOverviewTag,
    TicketOverviewUser, TicketOverviewAssignment
} from '../../ticket-overview/ticket-overview';

const Awesomplete = require('awesomplete/awesomplete');

export type CommentTextviewSaveEvent = {
    commands: imm.List<grammar.Cmd>,
    text: string,
}

@Component({
    selector: 'tt-command-search',
    templateUrl: './command-search.component.html',
    styleUrls: ['./command-search.component.scss']
})
export class CommandSearchComponent {
    @Input() projectId: string;
    @Input() assignedUsers: imm.Map<TicketOverviewUser, imm.List<TicketOverviewAssignment>>;
    @Input() allTicketTags: imm.Map<string, TicketOverviewTag>;
    @Input() allAssignmentTags: imm.Map<string, TicketOverviewAssTag>;

    public selected: string[] = [];
    public elementRef: ElementRef;
    public query: string = '';
    public filters: string[] = ['!#:', '!name:', '!tag:', '!user:', '!progress:', '!due:', 'test', 'abcd'];
    public filteredList: string[] = [];

    constructor(myElement: ElementRef) {
        this.elementRef = myElement;
    }
    select(item: string) {
        this.selected.push(item);
        this.query = '';
        this.filteredList = [];

    }

    filter() {
        let filter: HTMLInputElement = this.elementRef.nativeElement.querySelector('#filterInput');
        let result = new Awesomplete('input[data-multiple]', {
            list: this.filters,
            minChars: 1,
            autoFirst: true,
            filter: function (text: string, input: any) {
                return Awesomplete.FILTER_CONTAINS(text, input.match(/[^,]*$/)[0]);
            },
            replace: function (text: string) {
                let before = filter.value.match(/^.+,\s*|/)[0];
                filter.value = before + text;
            }

        });
        filter.select();
        result.open();
    }

}

