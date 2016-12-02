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
      (keydown.enter)='visible && save.emit()' (blur)='visible && save.emit()'
      (keydown.escape)='visible && abort.emit()'
     ></form>
  `,
})
export class EditTextviewTimeEditComponent implements TextviewEditComponent<number> {
  private _content: string;
  @Input() set content(v: number) {
      var d = moment.duration(v, 'ms');
       this._content = Math.floor(d.asHours())+":"+d.minutes();
  }
  get content() {
    console.log(moment.duration(this._content).asMilliseconds())

    return moment.duration(this._content).asMilliseconds();  
  }

  @Input() visible: boolean;
  @Output() readonly contentChange: EventEmitter<number> = new EventEmitter<number>();
  @Output() readonly abort: EventEmitter<void> = new EventEmitter<void>();
  @Output() readonly save: EventEmitter<void> = new EventEmitter<void>();

  onModelChange(val: string) {
    this.contentChange.emit(moment.duration(val).asMilliseconds());
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
