import { Component, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'tt-editable-textview',
  templateUrl: './editable-textview.component.html',
  styleUrls: ['./editable-textview.component.scss']
})
export class EditableTextviewComponent {
  @Input() text: string;
  @Input() editing: boolean = false;
  currentlyEditingText: string;
  @Output() textChange: EventEmitter<string> = new EventEmitter<string>();

  beginEditing(): void {
    this.editing = true;
    this.currentlyEditingText = this.text;
  }

  saveEdit(): void {
    this.editing = false;
    this.text = this.currentlyEditingText;
    this.textChange.emit(this.text);
  }

  abortEdit(): void {
    if (this.currentlyEditingText !== this.text) {
      // TODO request confirmation due to unsaved changes?
    }
    this.editing = false;
  }
}
