import {
    Component, Input, ElementRef, OnInit
} from '@angular/core';
import * as grammar from '../../../service/command/grammar';
import * as imm from 'immutable';
import {
    TicketOverviewAssTag, TicketOverviewTag,
    TicketOverviewUser
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
export class CommandSearchComponent implements OnInit {
    @Input() allUsers: imm.Map<string, TicketOverviewUser>;
    @Input() allTicketTags: imm.Map<string, TicketOverviewTag>;
    @Input() allAssignmentTags: imm.Map<string, TicketOverviewAssTag>;

    public selected: string[] = [];
    public elementRef: ElementRef;
    public query: string = '';
    public filters: string[] = ['!#:', '!name:', '!tag:', '!user:', '!progress:', '!due:', '!status', 'open', 'closed', 'dd/MM/yyyy'];
    public filteredList: string[] = [];

    constructor(myElement: ElementRef) {
        this.elementRef = myElement;
    }

    ngOnInit(): void {
        this.allAssignmentTags.forEach(e => {
            this.filters.push(e.normalizedName);

        });
        this.allTicketTags.forEach(e => {
            this.filters.push(e.normalizedName);

        });
        this.allUsers.forEach(e => {
            this.filters.push(e.username);

        });
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
            minChars: 2,
            autoFirst: true,
            thisOb: this,
            filter: function (text: string, input: string) {
                if (input.charAt(input.length - 1) === ':' && input.length > 2) {
                    return text.indexOf('!') === -1 && Awesomplete.FILTER_CONTAINS(text, input.match(/[^:]*$/)[0]);

                } else if (input.charAt(input.length - 1) === ' ') {
                    return Awesomplete.FILTER_CONTAINS(text, input.match(/[^\s]*$/)[0]);

                } else {
                    return Awesomplete.FILTER_CONTAINS(text, input.match(/[^\s]*$/)[0]);
                }
            },
            replace: function (text: string) {
                let before = filter.value.match(/^.+(\s|:)\s*|/)[0];
                filter.value = before + text;
            }

        });
        filter.select();
        result.open();
    }

}

