import { Component, Input, Output, EventEmitter } from '@angular/core';
import { TextviewEditComponent, TextviewReadComponent } from './edit-textview.component';

@Component({
  selector: 'tt-edit-textview-datetime-read',
  template: '{{ content | ttFormatMoment }}',
})
export class EditTextviewDateTimeReadComponent implements TextviewReadComponent<number> {
  @Input() content: number;
  @Input() visible: boolean;
}

@Component({
  selector: 'tt-edit-textview-datetime-edit',
  template: `
    <input
      type='text'
      [ttFocus]='visible' [ttSelectAll]="visible"
      [ngModel]='_content' (ngModelChange)='onModelChange($event)'
      (keydown.enter)='visible && save.emit()' (blur)='visible && save.emit()'
      (keydown.escape)='visible && abort.emit()'
    >
  `,
})
export class EditTextviewDateTimeEditComponent implements TextviewEditComponent<number> {
  private _content: string;
  @Input() set content(v: number) {
    this._content = '' + v;
  }
  get content() {
    return parseInt(this._content, 10);
  }

  @Input() visible: boolean;
  @Output() readonly contentChange: EventEmitter<number> = new EventEmitter<number>();
  @Output() readonly abort: EventEmitter<void> = new EventEmitter<void>();
  @Output() readonly save: EventEmitter<void> = new EventEmitter<void>();

  onModelChange(val: string) {
    this.contentChange.emit(parseInt(val, 10));
  }
}

@Component({
  selector: 'tt-edit-textview-datetime',
  template: `
    <tt-edit-textview [content]="content" (contentChange)="contentChange.emit($event)" [transient]="transient">
      <tt-edit-textview-datetime-edit #edit class="textview-edit"></tt-edit-textview-datetime-edit>
      <tt-edit-textview-datetime-read #read class="textview-read"></tt-edit-textview-datetime-read>
    </tt-edit-textview>
  `,
})
export class EditTextviewDateTimeComponent {
  @Input() content: number;
  @Output() readonly contentChange: EventEmitter<number> = new EventEmitter<number>();
  @Input() transient = false;
}
