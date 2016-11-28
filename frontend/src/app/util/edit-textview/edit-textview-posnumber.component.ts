import { Component, Input, Output, EventEmitter } from '@angular/core';
import { TextviewEditComponent, TextviewReadComponent } from './edit-textview.component';

@Component({
  selector: 'tt-edit-textview-posnumber-read',
  template: '{{content}}',
})
export class EditTextviewPosNumberReadComponent implements TextviewReadComponent<number> {
  @Input() content: number;
  @Input() visible: boolean;
}

@Component({
  selector: 'tt-edit-textview-posnumber-edit',
  template: `
    <input
      type='number' min='0'
      [ttFocus]="visible" [ttSelectAll]="visible"
      [ngModel]='content' (ngModelChange)='contentChange.emit($event)'
      (keydown.enter)='visible && save.emit()' (blur)='visible && save.emit()'
      (keydown.escape)='visible && abort.emit()'
    >
  `,
})
export class EditTextviewPosNumberEditComponent implements TextviewEditComponent<number> {
  @Input() content: number;
  @Input() visible: boolean;
  @Output() readonly contentChange = new EventEmitter<number>();
  @Output() readonly abort: EventEmitter<void> = new EventEmitter<void>();
  @Output() readonly save: EventEmitter<void> = new EventEmitter<void>();
}

@Component({
  selector: 'tt-edit-textview-posnumber',
  template: `
    <tt-edit-textview [content]="content" (contentChange)="contentChange.emit($event)">
      <tt-edit-textview-posnumber-edit #edit class="textview-edit"></tt-edit-textview-posnumber-edit>
      <tt-edit-textview-posnumber-read #read class="textview-read"></tt-edit-textview-posnumber-read>
    </tt-edit-textview>
  `,
})
export class EditTextviewPosNumberComponent {
  @Input() content: number;
  @Output() readonly contentChange = new EventEmitter<number>();
}
