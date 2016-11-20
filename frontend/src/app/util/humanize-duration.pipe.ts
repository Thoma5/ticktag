import { Pipe, PipeTransform } from '@angular/core';
import * as moment from 'moment';

@Pipe({ name: 'ttHumanizeDuration' })
export class HumanizeDurationPipe implements PipeTransform {
    transform(value: number): string {
        return moment.duration(value, 'ms').humanize();
    }
}
