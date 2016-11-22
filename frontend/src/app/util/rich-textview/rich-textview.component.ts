import {
    Component, Input, Output, EventEmitter, ElementRef, AfterViewInit, OnInit,
    NgZone, ViewChild, OnChanges, SimpleChanges, OnDestroy
} from '@angular/core';
import { TicketTagResultJson, UserResultJson } from '../../api';
import * as grammar from './grammar';

const codemirror = require('codemirror');
require('codemirror/addon/hint/show-hint');

function using<T>(getKey: (obj: T) => any): (a: T, b: T) => number {
    return (a: T, b: T) => {
        let aVal = getKey(a);
        let bVal = getKey(b);
        if (aVal < bVal) {
            return -1;
        } else if (aVal > bVal) {
            return 1;
        } else {
            return 0;
        }
    };
}

const COMMAND_COMPLETIONS = grammar.COMMAND_STRINGS.map(c => {
    if (c === 'close' || c === 'reopen') {
        return `!${c}`;
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
    @Input() initialContent: string = '';
    @Input() allTicketTags: TicketTagResultJson[] = [];

    private content: string;
    private instance: any;
    private commands: grammar.Cmd[];

    constructor(private element: ElementRef) {
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
            this.updateCommands();  // TODO debounce
            this.showHints();  // TODO debounce
        });
    }

    ngOnChanges(changes: SimpleChanges): void {
        if (this.instance && 'initialContent' in changes) {
            this.instance.setValue(changes['initialContent']);
        }
    }

    ngOnDestroy(): void {
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
                hint: () => {
                    return {
                        list: COMMAND_COMPLETIONS,
                        from: {line: cursor.line, ch: cursor.ch - isCommand[0].length + 1},
                        to: cursor,
                        selectedHint: COMMAND_COMPLETIONS.findIndex(c => c.startsWith(isCommand[1])),
                    };
                },
            });
            return;
        }

        let isAddTag = new RegExp(String.raw`${grammar.SEPERATOR_FRONT_REGEX}!tag:(${grammar.TAG_LETTER}*)$`, 'ui').exec(text);
        if (isAddTag) {
            this.instance.showHint({
                hint: () => {
                    return {
                        list: this.allTicketTags.map(tt => {
                            return tt.name;
                        }),
                        from: {line: cursor.line, ch: cursor.ch - isAddTag[1].length},
                        to: cursor,
                        selectedHint: this.allTicketTags.findIndex(tt => {
                            return tt.name.startsWith(isAddTag[1]);
                        }),
                    };
                }
            });
        }

        this.instance.showHint({
            hint: () => {
                return { list: new Array<string>() };
            }
        });
    }
}
