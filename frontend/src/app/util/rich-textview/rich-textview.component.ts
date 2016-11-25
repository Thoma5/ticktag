import {
    Component, Input, ElementRef, AfterViewInit, OnChanges, SimpleChanges,
    OnDestroy
} from '@angular/core';
import {
    TicketTagResultJson, UserResultJson, TimeCategoryJson, UserApi,
    AssignmentTagResultJson, TicketAssignmentJson, TicketApi, TicketResultJson
} from '../../api';
import { ApiCallService } from '../../service';
import { using } from '../using';
import * as grammar from './grammar';

const codemirror = require('codemirror');
require('codemirror/addon/hint/show-hint');

const COMMAND_COMPLETIONS = grammar.COMMAND_STRINGS.map(c => {
    if (c === 'close' || c === 'reopen') {
        return `!${c} `;
    } else {
        return `!${c}:`;
    }
}).sort(using<string>(c => c.replace('-', '')));

@Component({
    selector: 'tt-rich-textview',
    templateUrl: './rich-textview.component.html',
    styleUrls: ['./rich-textview.component.scss']
})
export class RichTextviewComponent implements AfterViewInit, OnChanges, OnDestroy {
    @Input() projectId: string;
    @Input() initialContent = '';
    @Input() allTicketTags = new Array<TicketTagResultJson>();
    @Input() ticketTags = new Array<TicketTagResultJson>();
    @Input() allTimeCategories = new Array<TimeCategoryJson>();
    @Input() allAssignmentTags = new Array<AssignmentTagResultJson>();
    @Input() assignedUsers = new Array<UserResultJson>();
    @Input() assignments = new Array<TicketAssignmentJson>();

    private content: string;
    private instance: any;
    private commands: grammar.Cmd[];

    private refreshTimeout: number = null;

    constructor(
        private element: ElementRef,
        private userApi: UserApi,
        private apiCallService: ApiCallService,
        private ticketApi: TicketApi) {
    }

    ngAfterViewInit(): void {
        let ta: HTMLTextAreaElement = this.element.nativeElement.querySelector('textarea');
        ta.value = this.initialContent || '';
        this.content = this.initialContent || '';
        this.instance = codemirror.fromTextArea(ta, {
            mode: 'text',
            lineWrapping: true,
            autofocus: false,
        });
        this.instance.on('changes', () => {
            this.content = this.instance.getValue();
            if (this.refreshTimeout === null) {
                this.refreshTimeout = window.setTimeout(() => {
                    this.refreshTimeout = null;
                    this.updateCommands();
                    this.showHints();
                }, 100);
            }
        });
    }

    ngOnChanges(changes: SimpleChanges): void {
        if (this.instance && 'initialContent' in changes) {
            this.instance.setValue(changes['initialContent']);
        }
    }

    ngOnDestroy(): void {
        window.clearTimeout(this.refreshTimeout);
        this.refreshTimeout = null;
        this.instance.toTextArea();
    }

    private updateCommands() {
        this.commands = grammar.extractCommands(this.content);
    }

    private showHints() {
        let cursor: {line: number, ch: number} = this.instance.getCursor();
        let text: string = this.instance.getRange({line: cursor.line, ch: 0}, cursor);

        let isCommand = new RegExp(String.raw`${grammar.SEPERATOR_FRONT_REGEX}(![a-z-]{0,10})$`, 'ui').exec(text);
        if (isCommand) {
            this.instance.showHint({
                hint: () => ({
                    list: COMMAND_COMPLETIONS,
                    from: {line: cursor.line, ch: cursor.ch - isCommand[1].length},
                    to: cursor,
                    selectedHint: Math.max(0, COMMAND_COMPLETIONS.findIndex(c => c.toLowerCase().startsWith(isCommand[1].toLowerCase()))),
                }),
                completeSingle: false,
            });
            return;
        }

        let isTicket = new RegExp(String.raw`${grammar.SEPERATOR_FRONT_REGEX}#(\S{0,20})$`, 'ui').exec(text);
        if (isTicket) {
            this.instance.showHint({
                hint: () => {
                    let projectId = this.projectId;
                    return this.apiCallService
                        .callNoError<TicketResultJson[]>(p => this.ticketApi.listTicketsFuzzyUsingGETWithHttpInfo(
                            projectId,
                            isTicket[1],
                            ['NUMBER_DESC', 'TITLE_ASC'],
                            p))
                        .map(tickets => ({
                            list: tickets.map(ticket => ({
                                text: `${ticket.number} `,
                                displayText: `#${ticket.number}: ${ticket.title}`,
                            })),
                            from: {line: cursor.line, ch: cursor.ch - isTicket[1].length},
                            to: cursor,
                            selectedHint: Math.max(0, tickets.findIndex(ticket => ('' + ticket.number).startsWith(isTicket[1]))),
                        }))
                        .toPromise();
                },
                completeSingle: false,
            });
            return;
        }

        let isAddRemoveTag = new RegExp(String.raw`${grammar.SEPERATOR_FRONT_REGEX}!(-?)tag:(${grammar.TAG_LETTER}*)$`, 'ui').exec(text);
        if (isAddRemoveTag) {
            let tags = isAddRemoveTag[1] ? this.ticketTags : this.allTicketTags;
            if (tags.length === 0) { return; }
            tags = tags.slice();
            tags.sort(using<TicketTagResultJson>(tag => tag.normalizedName));
            this.instance.showHint({
                hint: () => ({
                    list: tags.map(tt => tt.normalizedName + ' '),
                    from: {line: cursor.line, ch: cursor.ch - isAddRemoveTag[2].length},
                    to: cursor,
                    selectedHint: Math.max(0, tags.findIndex(tt => tt.normalizedName.startsWith(isAddRemoveTag[2].toLowerCase()))),
                }),
                completeSingle: false,
            });
            return;
        }

        let isTimeTag = new RegExp(
            String.raw`${grammar.SEPERATOR_FRONT_REGEX}!time:${grammar.TIME_REGEX}@(${grammar.TAG_LETTER}*)$`, 'ui'
        ).exec(text);
        if (isTimeTag) {
            if (this.allTimeCategories.length === 0) { return; }
            let cats = this.allTimeCategories.slice();
            cats.sort(using<TimeCategoryJson>(cat => cat.normalizedName));
            this.instance.showHint({
                hint: () => ({
                    list: cats.map(cat => cat.normalizedName + ' '),
                    from: {line: cursor.line, ch: cursor.ch - isTimeTag[1].length},
                    to: cursor,
                    selectedHint: Math.max(0, cats.findIndex(cat => cat.normalizedName.startsWith(isTimeTag[1].toLowerCase()))),
                }),
                completeSingle: false,
            });
            return;
        }

        let isAssign = new RegExp(String.raw`${grammar.SEPERATOR_FRONT_REGEX}!assign:(${grammar.USER_LETTER}*)$`, 'ui').exec(text);
        if (isAssign) {
            this.instance.showHint({
                hint: () => {
                    let projectId = this.projectId;
                    return this.apiCallService
                        .callNoError<UserResultJson[]>(p => this.userApi.listUsersFuzzyUsingGETWithHttpInfo(
                            projectId,
                            isAssign[1],
                            ['USERNAME_ASC', 'NAME_ASC', 'MAIL_ASC'],
                            p))
                        .map(users => ({
                            list: users.map(user => ({
                                text: user.username + '@',
                                displayText: `${user.username} (${user.name} <${user.mail}>)`,
                            })),
                            from: {line: cursor.line, ch: cursor.ch - isAssign[1].length},
                            to: cursor,
                            selectedHint: Math.max(0, users.findIndex(user => user.username.startsWith(isAssign[1].toLowerCase()))),
                        }))
                        .toPromise();
                },
                completeSingle: false,
            });
            return;
        }

        let isAssignTag = new RegExp(
            String.raw`${grammar.SEPERATOR_FRONT_REGEX}!assign:${grammar.USER_REGEX}?@(${grammar.TAG_LETTER}*)$`,
            'ui'
        ).exec(text);
        if (isAssignTag) {
            if (this.allAssignmentTags.length === 0) { return; }
            let tags = this.allAssignmentTags.slice();
            tags.sort(using<AssignmentTagResultJson>(tag => tag.normalizedName));
            this.instance.showHint({
                hint: () => ({
                    list: tags.map(tag => tag.normalizedName + ' '),
                    from: {line: cursor.line, ch: cursor.ch - isAssignTag[1].length},
                    to: cursor,
                    selectedHint: Math.max(0, tags.findIndex(tag => tag.normalizedName.startsWith(isAssignTag[1].toLowerCase()))),
                }),
                completeSingle: false,
            });
            return;
        }

        let isUnassign = new RegExp(
            String.raw`${grammar.SEPERATOR_FRONT_REGEX}!-assign:(${grammar.USER_LETTER}*(?:@${grammar.TAG_LETTER}*)?)$`,
            'ui'
        ).exec(text);
        if (isUnassign) {
            if (this.assignments.length === 0) { return; }
            let aus = this.assignments
                .map(a => ({
                    tag: this.allAssignmentTags.find(at => at.id === a.assignmentTagId),
                    user : this.assignedUsers.find(u => u.id === a.userId)
                }))
                .filter(a => a.tag && a.user)
                .map(as => ({
                    text: `${as.user.username}@${as.tag.normalizedName} `,
                    displayText: `${as.user.username}@${as.tag.normalizedName} (${as.user.name} <${as.user.mail}>)`,
                }));
            if (aus.length === 0) { return; }
            this.instance.showHint({
                hint: () => ({
                    list: aus,
                    from: {line: cursor.line, ch: cursor.ch - isUnassign[1].length},
                    to: cursor,
                    selectedHint: Math.max(0, aus.findIndex(au => au.text.startsWith(isUnassign[1].toLowerCase()))),
                }),
                completeSingle: false,
            });
            return;
        }
    }
}
