import { Pipe, PipeTransform } from '@angular/core';
import * as moment from 'moment';

@Pipe({ name: 'ttFormatMoment' })
export class FormatMomentPipe implements PipeTransform {
    transform(value: moment.Moment): string {
        return value.format('YYYY-MM-DD hh:mm:ss');
    }
}
