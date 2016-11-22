import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';

@Component({
  selector: 'tt-open-closed-button',
  templateUrl: './open-closed-button.component.html',
  styleUrls: ['./open-closed-button.component.scss']
})
export class OpenClosedButtonComponent implements OnInit {
  @Input() open: boolean;
  @Output() openChange: EventEmitter<boolean> = new EventEmitter<boolean>();
  buttonState: string = '0';

  ngOnInit(): void {
    // Hacks, hacks everywhere
    if (open) {
      this.buttonState = '1';
    } else {
      this.buttonState = '0';
    }
  }

  toggle(value: string): void {
    if (value === '1') {
      this.open = true;
    } else if (value === '0') {
      this.open = false;
    } else {
      console.log('Unknown state: ' + value);
    }
    this.openChange.emit(this.open);
  }
}
