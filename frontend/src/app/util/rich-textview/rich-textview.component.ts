import {
    Component, Input, Output, EventEmitter, ElementRef, AfterViewInit, OnInit,
    NgZone, ViewChild, OnChanges, SimpleChanges, OnDestroy
} from '@angular/core';
import * as grammar from './grammar';
let codemirror = require('codemirror');

@Component({
    selector: 'tt-rich-textview',
    templateUrl: './rich-textview.component.html',
    styleUrls: ['./rich-textview.component.scss']
})
export class RichTextviewComponent implements AfterViewInit, OnChanges, OnDestroy {
    @Input() initialContent: string;

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
            this.updateCommands();
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
}
