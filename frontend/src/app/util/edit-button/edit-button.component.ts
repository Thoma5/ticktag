import { Component, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'tt-edit-button',
  templateUrl: './edit-button.component.html',
  styleUrls: ['./edit-button.component.scss']
})
export class EditButtonComponent {
  @Input() visible: boolean = true;
  @Output() click: EventEmitter<void> = new EventEmitter<void>();

  onClick(): void {
    this.click.emit();
  }
}
