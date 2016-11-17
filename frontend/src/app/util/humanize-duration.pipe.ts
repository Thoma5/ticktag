import { Pipe, PipeTransform } from '@angular/core';
import * as moment from 'moment';

@Pipe({ name: 'ttHumanizeDuration' })
export class HumanizeDurationPipe implements PipeTransform {
    transform(value: moment.Duration): string {
        return value.humanize();
    }
}
