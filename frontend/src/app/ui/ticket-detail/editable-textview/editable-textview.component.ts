import { Component, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'tt-editable-textview',
  templateUrl: './editable-textview.component.html',
  styleUrls: ['./editable-textview.component.scss']
})
export class EditableTextviewComponent {
  @Input() text: string;
  _editing: boolean;
  currentlyEditingText: string;
  @Output() textChange: EventEmitter<string> = new EventEmitter<string>();
  @Output() editingChange: EventEmitter<boolean> = new EventEmitter<boolean>();

  @Input()
  set editing(editing: boolean) {
    if (editing) {
      this.prepareEdit();
    }
    this.updateEditing(editing);
  }

  get editing() {
    return this._editing;
  }

  updateEditing(editing: boolean): void {
    this._editing = editing;
    this.editingChange.emit(editing);
  }

  prepareEdit(): void {
      this.currentlyEditingText = this.text;
  }

  saveEdit(): void {
    this.text = this.currentlyEditingText;
    this.textChange.emit(this.text);
    this.updateEditing(false);
  }

  abortEdit(): void {
    if (this.currentlyEditingText !== this.text) {
      // TODO request confirmation due to unsaved changes?
    }
    this.updateEditing(false);
  }
}
