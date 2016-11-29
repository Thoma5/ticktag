import { Component, Input, Output, EventEmitter } from '@angular/core';
import { TextviewEditComponent, TextviewReadComponent } from './edit-textview.component';

@Component({
  selector: 'tt-edit-textview-string-read',
  template: '{{content}}',
})
export class EditTextviewStringReadComponent implements TextviewReadComponent<string> {
  @Input() content: string;
  @Input() visible: boolean;
}

@Component({
  selector: 'tt-edit-textview-string-edit',
  template: `
    <input
      type='text'
      [ttFocus]='visible' [ttSelectAll]="visible"
      [ngModel]='content' (ngModelChange)='contentChange.emit($event)'
      (keydown.enter)='visible && save.emit()' (blur)='visible && save.emit()'
      (keydown.escape)='visible && abort.emit()'
    >
  `,
})
export class EditTextviewStringEditComponent implements TextviewEditComponent<string> {
  @Input() content: string;
  @Input() visible: boolean;
  @Output() readonly contentChange: EventEmitter<string> = new EventEmitter<string>();
  @Output() readonly abort: EventEmitter<void> = new EventEmitter<void>();
  @Output() readonly save: EventEmitter<void> = new EventEmitter<void>();
}

@Component({
  selector: 'tt-edit-textview-string',
  template: `
    <tt-edit-textview [content]="content" (contentChange)="contentChange.emit($event)" [transient]="transient">
      <tt-edit-textview-string-edit #edit class="textview-edit"></tt-edit-textview-string-edit>
      <tt-edit-textview-string-read #read class="textview-read"></tt-edit-textview-string-read>
    </tt-edit-textview>
  `,
})
export class EditTextviewStringComponent {
  @Input() content: string;
  @Output() readonly contentChange: EventEmitter<string> = new EventEmitter<string>();
  @Input() transient = false;
}
