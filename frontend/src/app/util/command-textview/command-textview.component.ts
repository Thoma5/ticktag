import {
    Component, Input, ElementRef, AfterViewInit, OnChanges, SimpleChanges,
    OnDestroy, EventEmitter, Output
} from '@angular/core';
import { UserResultJson, UserApi, TicketApi, TicketResultJson } from '../../api';
import { ApiCallService } from '../../service';
import { using } from '../using';
import * as grammar from '../../service/command/grammar';
import * as imm from 'immutable';
import { Observable, Subscription } from 'rxjs';

const codemirror = require('codemirror');
require('codemirror/addon/hint/show-hint');

const COMMAND_COMPLETIONS = grammar.COMMAND_STRINGS.map(c => {
    if (c === 'close' || c === 'reopen') {
        return `!${c} `;
    } else {
        return `!${c}:`;
    }
}).sort(using<string>(c => c.replace('-', '')));

export type CommandTextviewSaveEvent = {
    commands: imm.List<grammar.Cmd>,
    text: string,
};

export type CommandTextviewTicketTag = {
    normalizedName: string,
};

export type CommandTextviewUser = {
    username: string,
    name: string,
    mail: string,
};

export type CommandTextViewUserAssignment = {
    tag: CommandTextviewAssignmentTag,
};

export type CommandTextviewTimeCategory = {
    normalizedName: string,
};

export type CommandTextviewAssignmentTag = {
    normalizedName: string,
};

@Component({
    selector: 'tt-command-textview',
    templateUrl: './command-textview.component.html',
    styleUrls: ['./command-textview.component.scss']
})
export class CommandTextviewComponent implements AfterViewInit, OnChanges, OnDestroy {
    @Input() initialContent: string;
    @Input() projectId: string;
    @Input() activeTags: imm.List<CommandTextviewTicketTag>;
    @Input() assignedUsers: imm.Map<CommandTextviewUser, imm.List<CommandTextViewUserAssignment>>;
    @Input() allTicketTags: imm.Map<string, CommandTextviewTicketTag>;
    @Input() allTimeCategories: imm.Map<string, CommandTextviewTimeCategory>;
    @Input() allAssignmentTags: imm.Map<string, CommandTextviewAssignmentTag>;
    @Input() resetEventObservable: Observable<string> | null = null;
    @Input() noCommands: boolean = false;
    @Input() hasShortcut: boolean = false;
    @Input() realInitialContent: string = '';

    @Output() readonly contentChange = new EventEmitter<CommandTextviewSaveEvent>();
    @Output() readonly save = new EventEmitter<void>();

    private content = '';
    private instance: any = null;
    private commands = imm.List<grammar.Cmd>();
    private resetEventSubscription: Subscription | null = null;

    private refreshTimeout: number = null;

    constructor(
        private element: ElementRef,
        private userApi: UserApi,
        private apiCallService: ApiCallService,
        private ticketApi: TicketApi) {
    }

    ngAfterViewInit(): void {
        let ta: HTMLTextAreaElement = this.element.nativeElement.querySelector('textarea');
        ta.value = '';
        this.content = '';
        this.instance = codemirror.fromTextArea(ta, {
            mode: 'text',
            lineWrapping: true,
            autofocus: false,
            extraKeys: {
                Tab: false,
                'Shift-Tab': false,
                'Ctrl-Enter': () => this.save.emit(),
            },
        });
        this.instance.on('changes', () => {
            this.content = this.instance.getValue();
            if (this.refreshTimeout === null) {
                this.refreshTimeout = window.setTimeout(() => {
                    this.refreshTimeout = null;
                    this.processChanges();
                }, 100);
            }
        });
        this.instance.setValue(this.realInitialContent || '');
    }

    ngOnChanges(changes: SimpleChanges): void {
        if ('resetEventObservable' in changes) {
            this.resubscribeResetEvent();
        }
        if (this.instance && 'realInitialContent' in changes) {
            this.instance.setText(this.realInitialContent);
        }
    }

    ngOnDestroy(): void {
        window.clearTimeout(this.refreshTimeout);
        this.refreshTimeout = null;
        this.instance.toTextArea();
        if (this.resetEventSubscription != null) {
            this.resetEventSubscription.unsubscribe();
        }
    }

    scrollToCommentInput(event: KeyboardEvent) {
        if (this.hasShortcut && event.altKey && event.key === 'c') {
            this.instance.scrollIntoView(this.instance);
            this.instance.focus();
        }
    }

    private resubscribeResetEvent() {
        if (this.resetEventSubscription != null) {
            this.resetEventSubscription.unsubscribe();
        }
        if (this.resetEventObservable != null) {
            this.resetEventSubscription = this.resetEventObservable.subscribe(val => {
                if (this.instance != null) {
                    this.instance.setValue(val);
                }
            });
        }
    }

    private processChanges(): void {
        this.updateCommands();
        this.showHints();
        this.emitChange();
    }

    private emitChange(): void {
        this.contentChange.emit({
            commands: this.commands,
            text: this.content,
        });
    }

    private updateCommands() {
        this.commands = grammar.extractCommands(this.content);
        if (this.noCommands) {
            this.commands = this.commands.filter(cmd => cmd.cmd === 'refTicket').toList();
        }
    }

    private showHints() {
        let cursor: { line: number, ch: number } = this.instance.getCursor();
        let text: string = this.instance.getRange({ line: cursor.line, ch: 0 }, cursor);

        let isCommand = new RegExp(String.raw`${grammar.SEPERATOR_FRONT_REGEX}(![a-z-]{0,10})$`, 'ui').exec(text);
        if (!this.noCommands && isCommand) {
            this.instance.showHint({
                hint: () => ({
                    list: COMMAND_COMPLETIONS,
                    from: { line: cursor.line, ch: cursor.ch - isCommand[1].length },
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
                            from: { line: cursor.line, ch: cursor.ch - isTicket[1].length },
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
        if (!this.noCommands && isAddRemoveTag) {
            let tags = (isAddRemoveTag[1] ? this.activeTags.valueSeq() : this.allTicketTags)
                .valueSeq()
                .sort(using<CommandTextviewTicketTag>(tag => tag.normalizedName))
                .toArray();
            if (tags.length === 0) { return; }
            tags = tags.slice();
            tags.sort(using<CommandTextviewTicketTag>(tag => tag.normalizedName));
            this.instance.showHint({
                hint: () => ({
                    list: tags.map(tt => tt.normalizedName + ' '),
                    from: { line: cursor.line, ch: cursor.ch - isAddRemoveTag[2].length },
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
        if (!this.noCommands && isTimeTag) {
            let cats = this.allTimeCategories
                .valueSeq()
                .sort(using<CommandTextviewTimeCategory>(cat => cat.normalizedName))
                .toArray();
            if (cats.length === 0) { return; }
            this.instance.showHint({
                hint: () => ({
                    list: cats.map(cat => cat.normalizedName + ' '),
                    from: { line: cursor.line, ch: cursor.ch - isTimeTag[1].length },
                    to: cursor,
                    selectedHint: Math.max(0, cats.findIndex(cat => cat.normalizedName.startsWith(isTimeTag[1].toLowerCase()))),
                }),
                completeSingle: false,
            });
            return;
        }

        let isAssign = new RegExp(String.raw`${grammar.SEPERATOR_FRONT_REGEX}!assign:(${grammar.USER_LETTER}*)$`, 'ui').exec(text);
        if (!this.noCommands && isAssign) {
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
                            from: { line: cursor.line, ch: cursor.ch - isAssign[1].length },
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
        if (!this.noCommands && isAssignTag) {
            let tags = this.allAssignmentTags
                .valueSeq()
                .sort(using<CommandTextviewAssignmentTag>(tag => tag.normalizedName))
                .toArray();
            if (tags.length === 0) { return; }
            this.instance.showHint({
                hint: () => ({
                    list: tags.map(tag => tag.normalizedName + ' '),
                    from: { line: cursor.line, ch: cursor.ch - isAssignTag[1].length },
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
        if (!this.noCommands && isUnassign) {
            let aus = this.assignedUsers
                .map((assignments, user) => assignments.map(ass => ({
                    text: `${user.username}@${ass.tag.normalizedName} `,
                    displayText: `${user.username}@${ass.tag.normalizedName} (${user.name} <${user.mail}>)`,
                })))
                .valueSeq()
                .flatMap(it => it)
                .toArray();
            if (aus.length === 0) { return; }
            this.instance.showHint({
                hint: () => ({
                    list: aus,
                    from: { line: cursor.line, ch: cursor.ch - isUnassign[1].length },
                    to: cursor,
                    selectedHint: Math.max(0, aus.findIndex(au => au.text.startsWith(isUnassign[1].toLowerCase()))),
                }),
                completeSingle: false,
            });
            return;
        }

        this.instance.showHint({
            hint: () => ({
                list: new Array<string>(),
                from: cursor,
                to: cursor
            }),
            completeSingle: false,
        });
    }
}
