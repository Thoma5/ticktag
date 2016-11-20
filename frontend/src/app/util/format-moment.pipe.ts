import { Pipe, PipeTransform } from '@angular/core';
import * as moment from 'moment';

@Pipe({ name: 'ttFormatMoment' })
export class FormatMomentPipe implements PipeTransform {
    transform(value: number): string {
        return moment(value * 1000).format('YYYY-MM-DD HH:mm:ss');
    }
}
