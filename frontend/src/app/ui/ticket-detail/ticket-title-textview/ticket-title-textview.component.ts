import { Component, Input, Output, EventEmitter } from '@angular/core';
import { TextviewEditComponent, TextviewReadComponent } from '../../../util/edit-textview/edit-textview.component';

@Component({
    selector: 'tt-title-textview-read',
    template: '{{content}}',
})
export class TitleTextviewReadComponent implements TextviewReadComponent<string> {
    @Input() content: string;
}

@Component({
    selector: 'tt-title-textview-edit',
    templateUrl: './ticket-title-textview-edit.component.html',
    styleUrls: ['./ticket-title-textview-edit.component.scss']
})
export class TitleTextviewEditComponent implements TextviewEditComponent<string> {
    @Input() active: boolean;
    @Input() content: string;
    @Output() contentChange: EventEmitter<string> = new EventEmitter<string>();
    @Output() abort: EventEmitter<void> = new EventEmitter<void>();
    @Output() save: EventEmitter<void> = new EventEmitter<void>();
}
