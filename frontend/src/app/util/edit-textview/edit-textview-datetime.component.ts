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
      type='datetime-local'
      [ttFocus]='visible' [ttSelectAll]="visible"
      [value]='_content' (ngModelChange)='onChange($event)'
      (keydown.enter)='visible && save.emit()' (blur)='visible && save.emit()'
      (keydown.escape)='visible && abort.emit()'
    >
  `,
})
export class EditTextviewDateTimeEditComponent implements TextviewEditComponent<number> {
  private _content: string;
  @Input() set content(v: number) {
    this._content = new Date(v).toISOString().split('.')[0];
  }
  get content() {
    return new Date(this._content).getTime();
  }

  @Input() visible: boolean;
  @Output() readonly contentChange: EventEmitter<number> = new EventEmitter<number>();
  @Output() readonly abort: EventEmitter<void> = new EventEmitter<void>();
  @Output() readonly save: EventEmitter<void> = new EventEmitter<void>();

  onChange(content: string) {
    if (content) {
      this.contentChange.emit(new Date(this._content).getTime());
    } else {
      this.contentChange.emit(null);
    }
  }
}

@Component({
  selector: 'tt-edit-textview-datetime',
  template: `
    <tt-edit-textview [content]="content" (contentChange)="contentChange.emit($event)">
      <tt-edit-textview-datetime-edit #edit class="textview-edit"></tt-edit-textview-datetime-edit>
      <tt-edit-textview-datetime-read #read class="textview-read"></tt-edit-textview-datetime-read>
    </tt-edit-textview>
  `,
})
export class EditTextviewDateTimeComponent {
  @Input() content: number;
  @Output() readonly contentChange: EventEmitter<number> = new EventEmitter<number>();
}
