import { Pipe, PipeTransform } from '@angular/core';
import * as moment from 'moment';

@Pipe({ name: 'ttFormatMoment' })
export class FormatMomentPipe implements PipeTransform {
    transform(value: number): string {
        return moment(value).format('YYYY-MM-DD');
    }
}
