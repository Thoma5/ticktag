import { Component, Input, Output, EventEmitter } from '@angular/core';
import { TextviewEditComponent, TextviewReadComponent } from '../../../util/edit-textview/edit-textview.component';

@Component({
    selector: 'tt-storypoints-textview-read',
    template: '<div class="points">{{content}}</div>',
    styleUrls: ['./ticket-storypoints.component.scss'],
})
export class StorypointsTextviewReadComponent implements TextviewReadComponent<number> {
    @Input() content: number;
}

@Component({
    selector: 'tt-storypoints-textview-edit',
    template: `<input type='number' [ttSelectAll] [ttFocus]='active' [ngModel]='content' (ngModelChange)='contentChange.emit($event)'
    (keydown.enter)='save.emit()'(blur)='abort.emit()'>`,
    styleUrls: ['./ticket-storypoints.component.scss']
})
export class StorypointsTextviewEditComponent implements TextviewEditComponent<number> {
    @Input() active: boolean;
    @Input() content: number;
    @Output() contentChange: EventEmitter<number> = new EventEmitter<number>();
    @Output() abort: EventEmitter<void> = new EventEmitter<void>();
    @Output() save: EventEmitter<void> = new EventEmitter<void>();
}


@Component({
    selector: 'tt-ticket-storypoints',
    templateUrl: './ticket-storypoints.component.html',
    styleUrls: ['./ticket-storypoints.component.scss']
})
export class TicketStorypointsComponent {
    @Input() points: number;
    @Output() pointsChange: EventEmitter<number> = new EventEmitter<number>();
    editing: boolean = false;
}
