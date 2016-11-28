import { Component, Input } from '@angular/core';

@Component({
  selector: 'tt-progressbar',
  templateUrl: './progressbar.component.html',
  styleUrls: ['./progressbar.scss']
})
export class ProgressBarComponent {
  @Input() fraction: number = 50;
  @Input() totalValue: number = 100;
  @Input() showNumbersOutside: boolean = false;

  fractionPercent(): number {
    if (this.totalValue === 0) {
      return 50;
    }
    return this.fraction / this.totalValue * 100;
  }
}
