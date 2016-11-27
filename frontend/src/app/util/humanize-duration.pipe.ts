import { Pipe, PipeTransform } from '@angular/core';
import * as moment from 'moment';

// See https://github.com/moment/moment/issues/348
moment.locale('precise-short-en', {
  relativeTime: {
    future: 'in %s',
    past: '%s ago',
    s: '%d s', // see https://github.com/timrwood/moment/pull/232#issuecomment-4699806
    m: '%d m',
    mm: '%d m',
    h: '%d h',
    hh: '%d h',
    d: '%d d',
    dd: '%d d',
    M: 'a month',
    MM: '%d months',
    y: 'a year',
    yy: '%d years'
  }
});


@Pipe({ name: 'ttHumanizeDuration' })
export class HumanizeDurationPipe implements PipeTransform {
  transform(value: number): string {
    moment.lang('precise-short-en');
    return moment.duration(value, 'ms').humanize();
  }
}
