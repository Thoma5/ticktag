import { Component, Input, Output, EventEmitter } from '@angular/core';
import { TextviewEditComponent, TextviewReadComponent } from './edit-textview.component';
import * as moment from 'moment';

@Component({
  selector: 'tt-edit-textview-time-read',
  template: '{{ content | ttHumanizeDuration }}',
})
export class EditTextviewTimeReadComponent implements TextviewReadComponent<number> {
  @Input() content: number;
  @Input() visible: boolean;
}

@Component({
  selector: 'tt-edit-textview-time-edit',
  template: `
     <input
      type='text'
      [ttFocus]='visible' [ttSelectAll]="visible"
      [ngModel]='_content' (ngModelChange)='onModelChange($event)'
      (keydown.enter)='visible && trySave()' (blur)='visible && trySave()'
      (keydown.escape)='visible && abort.emit()'
      [style.border]="valid ? '' : '3px solid red'"
    >
  `,
})
export class EditTextviewTimeEditComponent implements TextviewEditComponent<number> {
  private _content: string;
  @Input() set content(v: number) {
    const d = moment.duration(v, 'ms');
    this._content = Math.floor(d.asHours()) + ':' + (d.minutes() < 10 ? '0' : '') + d.minutes();
  }
  get content() {
    return moment.duration(this._content).asMilliseconds();
  }
  valid: boolean = true;
  @Input() visible: boolean;
  @Output() readonly contentChange: EventEmitter<number> = new EventEmitter<number>();
  @Output() readonly abort: EventEmitter<void> = new EventEmitter<void>();
  @Output() readonly save: EventEmitter<void> = new EventEmitter<void>();

  onModelChange(val: string) {
    const regexp = new RegExp('^[0-9]+(:[0-5][0-9])+$');
    this.valid = regexp.test(val);
    if (this.valid) {
      this.contentChange.emit(moment.duration(val).asMilliseconds());
    }
  }

  trySave() {
    if (this.valid) {
      this.save.emit();
    }
  }
}

@Component({
  selector: 'tt-edit-textview-time',
  template: `
    <tt-edit-textview [content]="content" (contentChange)="contentChange.emit($event)" [transient]="transient">
      <tt-edit-textview-time-edit #edit class="textview-edit"></tt-edit-textview-time-edit>
      <tt-edit-textview-time-read #read class="textview-read"></tt-edit-textview-time-read>
    </tt-edit-textview>
  `,
})
export class EditTextviewTimeComponent {
  @Input() content: number;
  @Output() readonly contentChange: EventEmitter<number> = new EventEmitter<number>();
  @Input() transient = false;
}
