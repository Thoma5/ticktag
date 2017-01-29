import { Component, Input, Output, EventEmitter } from '@angular/core';
import { TextviewEditComponent, TextviewReadComponent } from './edit-textview.component';
import * as moment from 'moment';


@Component({
  selector: 'tt-edit-textview-datetime-read',
  template: `
    <span *ngIf="content !== null && content !== undefined">{{ content | ttFormatMoment }}</span>
    <span *ngIf="content === null || content === undefined"></span>
  `,
})
export class EditTextviewDateTimeReadComponent implements TextviewReadComponent<number> {
  @Input() content: number;
  @Input() visible: boolean;
}

@Component({
  selector: 'tt-edit-textview-datetime-edit',
  template: `
    <input
      placeholder='YYYY-MM-DD'
      type='text'
      [ttFocus]='visible' [ttSelectAll]="visible"
      [ngModel]='_content' (ngModelChange)='onModelChange($event)'
      (keydown.enter)='visible && trySave()' (blur)='visible && trySave()'
      (keydown.escape)='visible && abort.emit()'
      [style.border]="valid ? '' : '3px solid red'"
    >
  `,
})
export class EditTextviewDateTimeEditComponent implements TextviewEditComponent<number> {
  private _content: string;
  @Input() set content(v: number) {
    this._content = (v !== null && v !== undefined) ? moment(v).format('YYYY-MM-DD') : '';
  }
  get content() {
    return this._content ? moment(this._content + ' +0000', 'YYYY-MM-DD ZZ').valueOf() : null;
  }

  valid: boolean = true;
  @Input() visible: boolean;
  @Input() nullable: boolean;
  @Output() readonly contentChange: EventEmitter<number> = new EventEmitter<number>();
  @Output() readonly abort: EventEmitter<void> = new EventEmitter<void>();
  @Output() readonly save: EventEmitter<void> = new EventEmitter<void>();

  onModelChange(val: string) {
    if (this.nullable && val.length === 0) {
      this.contentChange.emit(null);
      return;
    }

    const regexp = new RegExp('\\d{4}-\\d{2}-\\d{2}');
    const m = moment(val, 'YYYY-MM-DD');
    this.valid = regexp.test(val) && m.isValid();
    if (this.valid) {
      this.contentChange.emit(m.valueOf());
    }
  }

  trySave() {
    if (this.valid) {
      this.save.emit();
    }
  }
}


@Component({
  selector: 'tt-edit-textview-datetime',
  template: `
    <tt-edit-textview [editable]="editable" [content]="content" (contentChange)="contentChange.emit($event)" [transient]="transient">
      <tt-edit-textview-datetime-edit #edit class="textview-edit" [nullable]="nullable"></tt-edit-textview-datetime-edit>
      <tt-edit-textview-datetime-read #read class="textview-read"></tt-edit-textview-datetime-read>
    </tt-edit-textview>
  `,
})
export class EditTextviewDateTimeComponent {
  @Input() content: number;
  @Output() readonly contentChange: EventEmitter<number> = new EventEmitter<number>();
  @Input() transient = false;
  @Input() nullable = false;
  @Input() editable = true;
}
