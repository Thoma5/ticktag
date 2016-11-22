import { Component, Input, Output, EventEmitter } from '@angular/core';
import { TextviewEditComponent } from '../edit-textview/edit-textview.component';

@Component({
    selector: 'tt-markdown-textview-edit',
    templateUrl: './markdown-textview-edit.component.html',
    styleUrls: ['./markdown-textview-edit.component.scss']
})
export class MarkdownTextviewEditComponent implements TextviewEditComponent<string> {
    @Input() active: boolean;
    @Input() content: string;
    @Output() contentChange: EventEmitter<string> = new EventEmitter<string>();
    @Output() abort: EventEmitter<void> = new EventEmitter<void>();
    @Output() save: EventEmitter<void> = new EventEmitter<void>();
}
