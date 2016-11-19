import { Component, Input, Output, EventEmitter } from '@angular/core';
import { TextviewEditComponent } from '../editable-textview/editable-textview.component';

@Component({
    selector: 'tt-markdown-textview-edit',
    templateUrl: './ticket-markdown-textview-edit.component.html',
    styleUrls: ['./ticket-markdown-textview-edit.component.scss']
})
export class MarkdownTextviewEditComponent implements TextviewEditComponent {
    @Input() text: string;
    @Output() textChange: EventEmitter<string> = new EventEmitter<string>();
    @Output() abort: EventEmitter<void> = new EventEmitter<void>();
    @Output() save: EventEmitter<void> = new EventEmitter<void>();
}
