import { Component, Input } from '@angular/core';

@Component({
  selector: 'tt-editable-textview',
  templateUrl: './editable-textview.component.html',
  styleUrls: ['./editable-textview.component.scss']
})
export class EditableTextviewComponent {
  @Input() text: string;
  @Input() editing: boolean = false;

  beginEditing(): void {
    this.editing = true;
  }

  endEditing(): void {
    this.editing = false;
  }
}
