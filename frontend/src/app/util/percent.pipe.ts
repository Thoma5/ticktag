import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'ttPercent' })
export class PercentPipe implements PipeTransform {
  transform(value: number): string {
    return (value * 100).toLocaleString('en-US', {maximumFractionDigits: 0}) + '\xa0%';
  }
}
