import { Component, Input } from '@angular/core';

@Component({
  selector: 'tt-spinner',
  templateUrl: './spinner.component.html',
  styleUrls: ['./spinner.component.scss']
})
export class SpinnerComponent {
  @Input()
  color: string = '#333';
}
