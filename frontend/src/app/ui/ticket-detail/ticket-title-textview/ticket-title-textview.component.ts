import { Component, Input, Output, EventEmitter } from '@angular/core';
import { TextviewEditComponent, TextviewReadComponent } from '../editable-textview/editable-textview.component';

@Component({
    selector: 'tt-title-textview-read',
    template: '{{text}}',
})
export class TitleTextviewReadComponent implements TextviewReadComponent {
    @Input() text: string;
}

@Component({
    selector: 'tt-title-textview-edit',
    templateUrl: './ticket-title-textview-edit.component.html',
    styleUrls: ['./ticket-title-textview-edit.component.scss']
})
export class TitleTextviewEditComponent implements TextviewEditComponent {
    @Input() active: boolean;
    @Input() text: string;
    @Output() textChange: EventEmitter<string> = new EventEmitter<string>();
    @Output() abort: EventEmitter<void> = new EventEmitter<void>();
    @Output() save: EventEmitter<void> = new EventEmitter<void>();
}
